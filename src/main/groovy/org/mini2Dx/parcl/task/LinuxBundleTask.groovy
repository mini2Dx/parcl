/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Thomas Cashman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.mini2Dx.parcl.task

import org.mini2Dx.parcl.ParclUtils;
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory

import groovy.text.SimpleTemplateEngine

import java.io.File;
import java.io.FileWriter;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import static org.mini2Dx.parcl.ParclUtils.*

/**
 * Task for bundling an application into a Linux binary
 */
class LinuxBundleTask extends DefaultTask {
	LinuxBundleTask() {
		super()
		dependsOn("installApp")
	}

	@TaskAction
	def bundleBin() {
		File outputDirectory = new File(project.buildDir, "linux")
		createOutputDir(outputDirectory)
		copyJars(outputDirectory)

		String javaHome = project.getExtensions().findByName('parcl').linux.javaHome
		boolean includeJre = javaHome != null && javaHome.length() > 0
		if(includeJre) {
			copyJre(outputDirectory, javaHome)
		}
		createBin(outputDirectory, includeJre)
	}

	def copyJars(File outputDirectory) {
		File targetDirectory = new File(outputDirectory, "libs")
		createOutputDir(targetDirectory)

		project.copy {
			from getOutputJarsDirectory().getAbsolutePath()
			include "*.jar"
			into targetDirectory.getAbsolutePath()
		}
	}

	def copyJre(File outputDirectory, String javaHome) {
		File jreFolder = getJreFolder(javaHome)

		File targetDirectory = new File(outputDirectory, "jre")
		createOutputDir(targetDirectory)

		println "Copying JRE from " + jreFolder.getAbsolutePath() + " into " + targetDirectory.getAbsolutePath()

		project.copy {
			from jreFolder.getAbsolutePath()
			into targetDirectory.getAbsolutePath()
		}
	}

	def createBin(File outputDirectory, boolean includesJre) {
		InputStreamReader binTemplateReader = new InputStreamReader(ParclUtils.class.getClassLoader()
				.getResourceAsStream("application.sh.template"))

		HashMap<String, Object> scopes = new HashMap<String, Object>()
		scopes.put("preferSystemJre", project.getExtensions().findByName('parcl').linux.preferSystemJre)
		scopes.put("includesJre", includesJre)
		scopes.put("vmArgs", getVmArgs())
		scopes.put("appArgs", getAppArgs())
		scopes.put("mainClassName", project.convention.plugins.application.mainClassName)
		scopes.put("classpath", getClasspath(outputDirectory))

		try {
			File binFile = new File(outputDirectory, project.getExtensions().findByName('parcl').linux.binName)
			Writer writer = new FileWriter(binFile)
			MustacheFactory mustacheFactory = new DefaultMustacheFactory()
			Mustache mustache = mustacheFactory.compile(binTemplateReader, "application.sh")
			mustache.execute(writer, scopes)
			writer.flush()
			writer.close()

			Runtime.getRuntime().exec("chmod -R +x " + outputDirectory.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			binTemplateReader.close()
		}
	}

	def createOutputDir(File outputDirectory) {
		if(outputDirectory.exists()) {
			outputDirectory.deleteDir()
		}
		outputDirectory.mkdir()
	}

	File getOutputJarsDirectory() {
		File installDir = new File(project.getBuildDir(), "install")
		File projectInstallDir = new File(installDir, project.name)
		return new File(projectInstallDir, "lib")
	}

	String getVmArgs() {
		return getArgs(project.getExtensions().findByName('parcl').linux.vmArgs)
	}

	String getAppArgs() {
		return getArgs(project.getExtensions().findByName('parcl').linux.appArgs)
	}

	String getArgs(List<String> args) {
		if(args == null) {
			return ""
		}

		StringBuilder stringBuilder = new StringBuilder()
		for(int i = 0; i < args.size(); i++) {
			stringBuilder.append(args.get(i))
			if(i < args.size() - 1) {
				stringBuilder.append(" ")
			}
		}
		return stringBuilder.toString()
	}

	String getClasspath(File outputDirectory) {
		StringBuilder stringBuilder = new StringBuilder()

		File jarDirectory = new File(outputDirectory, "libs")
		File [] jars = jarDirectory.listFiles()
		for(int i = 0; i < jars.length; i++) {
			File jar = jars[i]
			if(jar.isFile() && jar.getAbsolutePath().endsWith(".jar")) {
				stringBuilder.append('$APPLICATION_HOME/libs/' + jar.getName())
			}
			if(i < jars.length - 1) {
				stringBuilder.append(":")
			}
		}

		return stringBuilder.toString()
	}
}
