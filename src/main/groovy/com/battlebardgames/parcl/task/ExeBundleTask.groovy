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
package com.battlebardgames.parcl.task

import java.io.File;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WorkResult

/**
 * Task for bundling an application into a .exe for Windows
 */
class ExeBundleTask extends DefaultTask {

	ExeBundleTask() {
		super()
		dependsOn("installApp")
	}

	@TaskAction
	def bundleExe() {
		File outputDirectory = new File(project.buildDir, "windows")
		createOutputDir(outputDirectory)
		copyJars(outputDirectory)

		String javaHome = project.getExtensions().findByName('parcl').exe.javaHome
		boolean includeJre = javaHome != null && javaHome.length() > 0
		if(includeJre) {
			copyJre(outputDirectory, javaHome)
		}
		createExe(outputDirectory)
		createExeConfig(outputDirectory, includeJre)
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

	def createExe(File outputDirectory) {
		copyInternalFileToExternal("parcl.exe", new File(outputDirectory, project.getExtensions().findByName('parcl').exe.exeName + ".exe").getAbsolutePath())
		copyInternalFileToExternal("parcl.pdb", new File(outputDirectory, "parcl.pdb").getAbsolutePath())
	}

	def createExeConfig(File outputDirectory, boolean includeJre) {
		PrintWriter writer = new PrintWriter(new File(outputDirectory, "application.xml"))
		writer.println("<?xml version=\"1.0\"?>")
		writer.println("<application xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")

		writer.println("</application>")
		writer.flush()
		writer.close()
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

	File getJreFolder(String directory) {
		File directoryHandle = new File(directory)
		if(directory.contains("jre")) {
			return directoryHandle
		}

		File [] children = directoryHandle.listFiles()
		for (int i = 0; i < children.length; i++) {
			File child = children[i]
			if(child.isDirectory()) {
				if(child.getAbsolutePath().contains("jre")) {
					return child
				}
			}
		}
		return null
	}

	def copyInternalFileToExternal(String internalFilepath, String outputFilepath) {
		InputStream internalStream = ExeBundleTask.class.getClassLoader()
				.getResourceAsStream(internalFilepath)
		FileOutputStream outputStream
		try {
			outputStream = new FileOutputStream(outputFilepath)
			byte[] buffer = new byte[2048]
			int readResult = internalStream.read(buffer);
			while(readResult != -1) {
				outputStream.write(buffer, 0, readResult);
				readResult = internalStream.read(buffer);
			}
		} finally {
			if(outputStream != null) {
				outputStream.close();
			}
		}
	}
}
