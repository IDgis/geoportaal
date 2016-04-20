package nl.idgis.gradle.querydsl;

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.play.plugins.PlayPlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin

import groovy.sql.Sql

import com.querydsl.sql.Configuration
import com.querydsl.sql.SQLTemplates
import com.querydsl.sql.PostgreSQLTemplates
import com.querydsl.sql.codegen.MetaDataExporter
import com.mysema.codegen.model.TypeCategory;
import com.mysema.codegen.model.Type;
import com.mysema.codegen.model.ClassType;
import com.querydsl.codegen.TypeMappings
import com.querydsl.codegen.JavaTypeMappings
import com.querydsl.codegen.EntityType
import java.util.UUID

/**
 * This plugins adds QueryDSL source generation capabilities to a project.
 * It performs the following functions:
 *
 *  - Generates QueryDSL metadata classes for a database schema described by
 *    a set of evolution files (using the Play convention of "ups" and "downs").
 *  - Runs the QueryDSL annotation processor on a given source directory. For example
 *    to generate projections.
 *
 * Also interacts with the Eclipse plugin: adds the generated source directory to the
 * eclipse classpath.
 *
 * Configured using @{link QueryDSPluginExtension}  
 */
class QueryDSLPlugin implements Plugin<Project> {
	void apply  (Project project) {
		project.extensions.create ("queryDSL", QueryDSLPluginExtension)
		
		// Add two configurations used to collect dependencies. One for the APT
		// compiler and one containing database drivers for the metadata generator.
		project.configurations {
			queryDSLApt
			queryDSLMetaDataExporter
		}

		// Add default dependencies to the configurations used by this plugin:
		project.dependencies {
			// APT processor for QueryDSL:
			queryDSLApt "com.querydsl:querydsl-apt:4.0.6"
		}	
			
		// Defer task creation until after the project has been evaluated: the configuration
		// settings for the JavaCompile task don't all accept closures and the extension
		// may not have been configured otherwise.
		project.afterEvaluate {
			
			def generateMetadataTask = project.task ('queryDSLGenerateMetadata') {
				dependsOn project.configurations.queryDSLMetaDataExporter.buildDependencies
				
				ext.srcDir = project.queryDSL.evolutionsDir
				ext.srcFiles = project.files { srcDir.listFiles () }
				ext.buildDbName = "${project.name}-build-${UUID.randomUUID()}".toString()
				ext.packageName = project.queryDSL.packageName
				ext.targetDir = new File (project.buildDir.absolutePath + File.separator + "queryDSL" + File.separator + "src")
				
				inputs.files srcFiles
				outputs.dir targetDir
				
				doLast {
					println "build database name: ${buildDbName}"
					
					// Add dependencies to classpath:
					URLClassLoader loader = GroovyObject.class.classLoader
					project.configurations.queryDSLMetaDataExporter.each { File file ->
						loader.addURL (file.toURL ())
					}
					
					def masterSql = Sql.newInstance ("jdbc:postgresql://${project.queryDSL.databaseHost}:5432/postgres", "postgres", "postgres", "org.postgresql.Driver")
					try {
						// Create the database
						masterSql.execute "create database \"" + buildDbName + "\""
					} finally {
						masterSql.close ()
					}
					
					// Populate the database:
					def sql = Sql.newInstance ("jdbc:postgresql://${project.queryDSL.databaseHost}:5432/${buildDbName}", "postgres", "postgres", "org.postgresql.Driver")
					try {
						// Extract revision number from file name:
						def rev = { file -> file.name.find(/(.*?)\.sql/, { name, rev -> rev }).toInteger() }
					
						srcFiles.sort { a, b ->  rev(a) - rev(b) }.each { file ->
							
							// Extract the "Ups" section from the SQL:
							StringBuilder builder = new StringBuilder ()
							boolean inUps = false;
							
							new File (file.getAbsolutePath()).readLines().each { line ->
								if (line.startsWith ("# --- !Ups")) {
									inUps = true
								} else if (line.startsWith ("# --- !Downs")) {
									inUps = false
								} else if (inUps) {
									line = line.replace(';;', ';')
								
									builder.append (line + "\n")
								}
							}
							
							sql.execute builder.toString ()
						}
					} finally {
						sql.close ()
					}
					
					// Generate QueryDSL metamodel
					sql = Sql.newInstance ("jdbc:postgresql://${project.queryDSL.databaseHost}:5432/${buildDbName}", "postgres", "postgres", "org.postgresql.Driver")
					try {
						def tsVectorClass = loader.loadClass ('nl.idgis.querydsl.TsVector')
						def tsVectorPathClass = loader.loadClass ('nl.idgis.querydsl.TsVectorPath')
					
						SQLTemplates templates = new PostgreSQLTemplates ()
						Configuration configuration = new Configuration (templates)
						configuration.registerType ('tsvector', tsVectorClass)
						
						TypeMappings typeMappings = new JavaTypeMappings ()
						Type type = new ClassType (tsVectorClass);
						typeMappings.register(type, new ClassType (tsVectorPathClass, type));
					
						MetaDataExporter exporter = new MetaDataExporter ()
						exporter.setImports ((String[])['static nl.idgis.querydsl.factory'])
						exporter.setPackageName packageName
						exporter.setTargetFolder targetDir
						exporter.setConfiguration (configuration)
						exporter.setTypeMappings (typeMappings)
						exporter.setTableTypesToExport 'TABLE,VIEW,MATERIALIZED VIEW'
						exporter.export sql.getConnection ().getMetaData ()   
					} finally {
						sql.close ()
					}
					
					masterSql = Sql.newInstance ("jdbc:postgresql://${project.queryDSL.databaseHost}:5432/postgres", "postgres", "postgres", "org.postgresql.Driver")
					try {
						// Drop the database
						masterSql.execute "drop database if exists \"" + buildDbName + "\""
					} finally {
						masterSql.close ()
					}
				}
			}
			
			// The QueryDSL annotation processor task:
			def annotationProcessorTask = project.task ('queryDSLAnnotationProcessor', type: JavaCompile, group: 'build', description: 'Generates QueryDSL projections') {
				dependsOn generateMetadataTask
				
				source = project.queryDSL.sourceDir
				classpath = project.configurations.queryDSLApt + project.queryDSL.classpath + project.files ("${project.buildDir}/queryDSL/src")
				options.compilerArgs = [
					"-proc:only",
					"-processor", "com.querydsl.apt.QuerydslAnnotationProcessor",
					"-sourcepath", project.file (project.queryDSL.sourceDir).getAbsolutePath() + File.pathSeparator + project.file ("${project.buildDir}/queryDSL/src").getAbsolutePath()
				]
				destinationDir = new File (project.buildDir, "queryDSL/src")
				
				// Set the output of the compile task to all java-files in the generated source directory. This ensures
				// that dependant tasks (such as compilePlayBinaryScala) only run if new source files have been generated
				// in this task.
				outputs.files { project.fileTree ("${project.buildDir}/queryDSL/src").include ("**/*.java") }
			}
			
			// QueryDSL main task:
			def queryDSLTask = project.task ('queryDSL') {
				dependsOn annotationProcessorTask
			}
			
			// Configure the Play! plugin if present: the QueryDSL generated source directory is added
			// as a Play! source directory.
			project.plugins.withType (PlayPlugin) {
				project.dependencies {
					play "com.querydsl:querydsl-sql:4.0.6"
				}
				
				project.model {
					components {
						play {
							sources {
								java {
									source.srcDir new File (project.buildDir, "queryDSL/src")
									
									// Indicate that these sources are generated by the QueryDSL metadata task:
									builtBy queryDSLTask
								}
							}
						}
					}
				}
			}
			
			project.plugins.withType (JavaPlugin) {
				project.dependencies {
					compile "com.querydsl:querydsl-sql:4.0.6"
				}
				project.sourceSets {
					main {
						java {
							srcDir new File (project.buildDir, "queryDSL/src")
						}
					}
				}
				
				project.getTasksByName('compileJava', false).each { compileTask ->
					compileTask.dependsOn queryDSLTask
				}
			}
			
			// Configure the Eclipse plugin if present: the QueryDSL generated source directory is
			// added as a source folder.
			project.plugins.withType (EclipsePlugin) {
				project.eclipse {
					classpath {
						file {
							whenMerged { cp ->
								cp.entries.add (new org.gradle.plugins.ide.eclipse.model.SourceFolder("build/queryDSL/src", null))
							}
						}
					}
				}
			}
			
		}
	}
}