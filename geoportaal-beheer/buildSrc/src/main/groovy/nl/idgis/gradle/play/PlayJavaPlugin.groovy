package nl.idgis.gradle.play;

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.play.plugins.PlayPlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin

import org.gradle.api.tasks.Copy

import nl.idgis.gradle.querydsl.QueryDSLPlugin

/**
 * This plugin adds additional functionality to Play builds that is not provided by the default
 * Gradle Play plugin. By additionaly including this plugin the following functionality is added
 * to the build:
 *
 *  - Project assets are packaged in a webjar as a part of the WAR-artifact for the project.
 *  - Less assets from webjars on which the project depends are extracted so that they can
 *    be included in the less scripts of this project.
 *  - Less sources in the app/assets folder are compiled using the less plugin:
 *    com.github.houbie.gradle.lesscss.LesscTask.
 *
 * This plugin also interacts with the following plugins that can be optionally included in 
 * the build:
 *
 *  - The QueryDSL plugin is configured by providing the default source and evolutions paths
 *    based on the Play convention.
 *  - The eclipse plugin is configured by provinding the correct source folders and class path.
 */  
class PlayJavaPlugin implements Plugin<Project> {
	void apply  (Project project) {
		// Apply the default Play plugin and the Java base plugin to provide basic
		// build functionality which will be extended by this plugin.
		project.pluginManager.apply (JavaBasePlugin)
		project.pluginManager.apply (PlayPlugin)
		
		// Configure additional defaults in the Play! model:
		project.model {
			components {
				play {
					sources {
						// Add the extracted webjars as a source directory:
						resources {
							source.srcDir "${project.buildDir}/webjar-source"
						}
					}

					// Add additional assets pipeline stages for each Play binary. 					
					binaries.all { binary ->
						binary.classes.addResourceDir project.file("${project.buildDir}/webjar-source")
						
						// Copy the public assets to the webjars folder.
						tasks.create ("copy${binary.name.capitalize()}WebjarPublicAssets", Copy) { task ->
							from 'public'
							into "${project.buildDir}/webjar-source/META-INF/resources/webjars/$task.project.name/$task.project.version"
							
							binary.classes.builtBy task
						}
						
						// Copy the uncompiled assets from the assets folder to the webjars folder.
						tasks.create ("copy${binary.name.capitalize()}WebjarAssets", Copy) { task ->
							from 'app/assets'
							into "${project.buildDir}/webjar-source/META-INF/resources/webjars/$task.project.name/$task.project.version"
							
							binary.classes.builtBy task
						}

						// Copy the compiled assets	(minified JS and LESS compiled to CSS) to the webjars folders:				
						tasks.create ("copy${binary.name.capitalize()}WebjarCompiledAssets", Copy) { task ->
							from "${project.buildDir}/${binary.name}/minify${binary.name.capitalize()}JavaScript"
							into "${project.buildDir}/webjar-source/META-INF/resources/webjars/$task.project.name/$task.project.version"
							
							binary.classes.builtBy task
						}
		
						def lessDestinationDir = "${project.buildDir}/less/"					
						def extractLessTask = "extract${binary.name.capitalize()}WebjarsLess"
						def copyLessTask = "copy${binary.name.capitalize()}Less"
						
						binary.assets.addAssetDir project.file (lessDestinationDir)
						
						// Extract all less scripts found in webjars on which this project depends
						// to a separate source folder:
						tasks.create (extractLessTask, Copy) { task ->
							from {
								project.configurations.play.collect { 
									project.zipTree(it).matching { include 'META-INF/resources/webjars/**' }
								}
							}
							into lessDestinationDir + "lib/"
							eachFile { details ->
								def shortPath = (details.path - "META-INF/resources/webjars/")
								def parts = shortPath.split '/'
								def result = new StringBuilder ()
								for (int i = 0; i < parts.length; ++ i) {
									if (i == 1) {
										continue;
									}
									if (result.length () > 0) {
										result.append "/"
									}
									result.append parts[i]
								}
								def targetPath = result.toString ()
								details.path = targetPath
							}
							
							binary.assets.builtBy task
						}

						// Copy the less scripts from this project to the LESS source folder before compilation.						
						tasks.create (copyLessTask, Copy) { task ->
							from "app/assets"
							into lessDestinationDir
							include "**/*.less"
						
							binary.assets.builtBy task
							dependsOn extractLessTask
						}

						// Compile the less code of this project and the webjars on which it depends:						
						tasks.create ("compile${binary.name.capitalize()}LessAssets", com.github.houbie.gradle.lesscss.LesscTask) { task ->
							sourceDir lessDestinationDir
							include "**/*.less"
							exclude "**/_*.less"
							exclude "lib/**"
							destinationDir = project.file("${project.buildDir}/${binary.name}/lessAssets")
							
							binary.assets.addAssetDir destinationDir
							binary.assets.builtBy task
							dependsOn copyLessTask
						}
					}
					
				}
			}
		}
		
		// Configure the QueryDSL plugin if present: the default source locations of Play!
		// are set as input for the QueryDSL processor, the evolutions dir is set
		// and the packageName is set to models.
		project.plugins.withType (QueryDSLPlugin) {
			project.queryDSL {
				evolutionsDir = project.file "conf/evolutions/default"
				sourceDir = project.file "app"
				packageName = "models"
				classpath = project.configurations.play 
			}			
		}
		
		project.afterEvaluate {
			// Configure the eclipse plugin if present: add Play! source directories and
			// set the correct project nature.
			// Configure the Eclipse plugin if present: the QueryDSL generated source directory is
			// added as a source folder.
			project.plugins.withType (EclipsePlugin) {
				project.eclipse {
					classpath {
						plusConfigurations += [ project.configurations.play ]
						
						file {
							whenMerged { cp ->
								cp.entries.add (new org.gradle.plugins.ide.eclipse.model.SourceFolder('app', null))
								cp.entries.add (new org.gradle.plugins.ide.eclipse.model.SourceFolder('test', null))
								cp.entries.add (new org.gradle.plugins.ide.eclipse.model.SourceFolder("build/playBinary/src/compilePlayBinaryRoutes", null))
								cp.entries.add (new org.gradle.plugins.ide.eclipse.model.SourceFolder("build/playBinary/src/compilePlayBinaryTwirlTemplates", null))
							}
						}
					}
					
					it.project {
						natures 'org.scala-ide.sdt.core.scalanature', 'org.eclipse.jdt.core.javanature'
					}
				}
			}
		}
	}	
}