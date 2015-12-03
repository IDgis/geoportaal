package nl.idgis.gradle.querydsl;

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.play.plugins.PlayPlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin

import groovy.sql.Sql
import com.querydsl.sql.codegen.MetaDataExporter

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
			queryDSLDatabaseDriver
		}

		// Add default dependencies to the configurations used by this plugin:
		project.dependencies {
			// Database drivers used for QueryDSL metadata generation:
			queryDSLDatabaseDriver "com.h2database:h2:1.4.190"
	
			// APT processor for QueryDSL:
			queryDSLApt "com.querydsl:querydsl-apt:4.0.6"
		}	
			
		// Defer task creation until after the project has been evaluated: the configuration
		// settings for the JavaCompile task don't all accept closures and the extension
		// may not have been configured otherwise.
		project.afterEvaluate {
		
			// Database creation task:
			def databaseCreationTask = project.task ('queryDSLCreateDatabase') {
				ext.srcDir = project.queryDSL.evolutionsDir
				ext.srcFiles = project.files { srcDir.listFiles () }
				ext.destFile = new File (new File (project.buildDir, "queryDSL"), "db")
				
				inputs.files srcFiles
				outputs.file new File (destFile.getAbsolutePath () + ".mv.db")
				
				doLast {
					// Load the database drivers:
					URLClassLoader loader = GroovyObject.class.classLoader
					project.configurations.queryDSLDatabaseDriver.each { File file ->
						loader.addURL (file.toURL ())
					}
					
					// Create and populate the database:
					def sql = Sql.newInstance ("jdbc:h2:/${destFile};DB_CLOSE_DELAY=0;FILE_LOCK=NO;MODE=PostgreSQL", "", "", "org.h2.Driver")
					sql.execute "drop all objects"
					try {
						srcFiles.collect { it.getAbsolutePath() }.sort ().each { file ->
							// Extract the "Ups" section from the SQL:
							StringBuilder builder = new StringBuilder ()
							boolean inUps = false;
							
							new File (file).readLines().each { line ->
								if (line.startsWith ("# --- !Ups")) {
									inUps = true
								} else if (line.startsWith ("# --- !Downs")) {
									inUps = false
								} else if (inUps) {
									builder.append (line + "\n")
								}
							}
							
							sql.execute builder.toString ()
						}
					} finally {
						sql.close ()
					}
				}
			}
			
			// Metadata generator task:
			def generateMetadataTask = project.task ('queryDSLGenerateMetadata') {
				dependsOn databaseCreationTask
				
				ext.packageName = project.queryDSL.packageName
				ext.targetDir = new File (project.buildDir.absolutePath + File.separator + "queryDSL" + File.separator + "src") 
				
				inputs.file new File (databaseCreationTask.destFile.getAbsolutePath () + ".mv.db")
				outputs.dir targetDir
			
				doLast {	
					// Load the database drivers:
					URLClassLoader loader = GroovyObject.class.classLoader
					project.configurations.queryDSLDatabaseDriver.each { File file ->
						loader.addURL (file.toURL ())
					}
					
					// Create and populate the database:
					def sql = Sql.newInstance ("jdbc:h2:/${databaseCreationTask.destFile};DB_CLOSE_DELAY=0;FILE_LOCK=NO;MODE=PostgreSQL", "", "", "org.h2.Driver")
					try {
						MetaDataExporter exporter = new MetaDataExporter ()
						exporter.setPackageName packageName
						exporter.setTargetFolder targetDir
						exporter.export sql.getConnection ().getMetaData ()   
					} finally {
						sql.close ()
					}
				}
			}
			
			// The QueryDSL annotation processor task:
			def annotationProcessorTask = project.task ('queryDSLAnnotationProcessor', type: JavaCompile, group: 'build', description: 'Generates QueryDSL projections') {
				dependsOn generateMetadataTask
				
				println "Setting source: " + project.queryDSL.sourceDir
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