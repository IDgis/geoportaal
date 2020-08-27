package nl.idgis.gradle.docker

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.play.plugins.PlayPlugin

import org.gradle.api.Task
import org.gradle.api.tasks.Copy

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

/**
 * Plugin that adds functionality to a build for constructing Docker images.
 * Currently it only supports creating images for Play! projects. Such project
 * must include at least the Gradle Play plugin.
 *
 * Adds the binary distribution of the Play project and on top of that includes
 * a custom launch script that adds any environment variables that have a dot
 * in the name as configuration settings (assuming the Play project uses
 * typesafe config).
 */
class DockerPlugin implements Plugin<Project> {
	void apply (Project project) {
		// Apply the Docker API plugin:
		project.pluginManager.apply (DockerRemoteApiPlugin)
		
		// Task for building all Docker images in the project. Must depend
		// on all individual tasks that are used to build docker images:
		def buildDockerImages = project.task ("buildDockerImages")
		
		// Configure the Docker build procedure for Play projects:
		project.plugins.withType (PlayPlugin) {
		
			// Create tasks that construct the Docker image to each Play binary. Each
			// task is added as a dependency to the buildDockerImages task. This allows
			// all docker images to be built by issueing a single Gradle command.
			project.model {
				components {
					play {
						binaries.all { binary ->
							def copyRootCA = "copyRootCA"
							def distTask = "create${binary.name.capitalize()}Dist"
							def copyUnzipBinaryTask = "copy${binary.name.capitalize()}Docker"
							def createDockerfileTask = "create${binary.name.capitalize()}Dockerfile"
							def buildImageTask = "build${binary.name.capitalize()}DockerImage"
							def copyScriptTask = "copy${binary.name.capitalize()}DockerStartupScript"
							
							// Copy the root certificate to the docker build context
							tasks.create (copyRootCA, Copy) { task ->
								from "../docker/rootCA.crt"
								into "${project.buildDir}/docker/${binary.name}"
							}
							
							// Copy and unzip the Play binary ZIP to the directory where the Docker build 
							// context is materialized:
							tasks.create (copyUnzipBinaryTask, Copy) { task ->
								task.dependsOn distTask
								
								from project.zipTree("${project.buildDir}/distributions/${binary.name}.zip")
								into "${project.buildDir}/docker/${binary.name}"
							}
							
							// Write a custom startup script to the Docker context directory. This
							// script takes care of adding environment variables as configuration
							// settings to the Play process:
							tasks.create (copyScriptTask, Task) { task ->
								task.dependsOn copyUnzipBinaryTask
								
								def content = this.getClass ().getResource ("/nl/idgis/gradle/docker/start-application.sh").text
								def output = project.file "${project.buildDir}/docker/${binary.name}/start-application.sh"
								
								task.outputs.file output
								
								task.doLast {
									output.write (content)
								}
							}
							
							// Generate a Dockerfile in the context directory:
							tasks.create (createDockerfileTask, Dockerfile) { task ->
								task.destFile = project.file "${project.buildDir}/docker/${binary.name}/Dockerfile"
								task.dependsOn distTask
								
								task.from "azul/zulu-openjdk:8"
								task.copyFile ("${binary.name}", "/opt/${binary.name}")
								task.copyFile ("start-application.sh", "/opt/start-application.sh")
								task.runCommand "chown -R daemon:daemon /opt/${binary.name}" +
									" && chmod +x /opt/${binary.name}/bin/${binary.name}" +
									" && mkdir /var/lib/${binary.name}" + 
									" && chown -R daemon:daemon /var/lib/${binary.name}" +
									" && usermod -d /var/lib/${binary.name} daemon" +
									" && chmod +x /opt/start-application.sh"
								
								if(project.hasProperty('importRootCA')) {
									task.copyFile "rootCA.crt", "/opt"
									task.runCommand "keytool -import -alias server -keystore /usr/lib/jvm/zulu8-ca-amd64/jre/lib/security/cacerts -file /opt/rootCA.crt -storepass changeit -noprompt"
								}
								
								task.environmentVariable ("JAVA_OPTS", "-Xmx2048m")
								task.environmentVariable ("PLAY_BINARY_OPTS", "-Dplay.evolutions.db.default.autoApply=true")
								task.workingDir "/opt/${binary.name}"
								task.user "daemon"
								task.defaultCommand ("/opt/start-application.sh", "/opt/${binary.name}/bin/${binary.name}") 
							}
							
							// Build the Docker image:
							tasks.create (buildImageTask, DockerBuildImage) { task->
								task.dependsOn createDockerfileTask
								task.dependsOn copyUnzipBinaryTask
								task.dependsOn copyScriptTask
								if(project.hasProperty('importRootCA')) {
									task.dependsOn copyRootCA
								}
								
								def tagVersion = "${project.version}".replaceAll ("\\+", "-")
								
								task.inputDir = project.file "${project.buildDir}/docker/${binary.name}"
								task.quiet = false
								task.tag = "idgis/${project.name}:${tagVersion}"
							}
							
							buildDockerImages.dependsOn buildImageTask
						}
					}
				}
			}
		}
	}
}