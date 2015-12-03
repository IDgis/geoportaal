package nl.idgis.gradle.querydsl

import java.io.File
import org.gradle.api.file.FileCollection

/**
 * Configuration project extension to support the QueryDSL plugin.
 */
class QueryDSLPluginExtension {
	/**
	 * Directory containing evolution files to use for metadata generation.
	 */
	def File evolutionsDir
	
	/**
	 * Source directory to use when running the QueryDSL annotation processor.
	 */ 
	def File sourceDir
	
	/**
	 * Package name for generated metadata classes. Defaults to "models".
	 */
	def String packageName = "models"
	
	/**
	 * Classpath to use for the QueryDSL annotation processor.
	 */
	def FileCollection classpath
}