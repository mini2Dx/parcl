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

import org.apache.tools.ant.types.FileSet
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.oracle.appbundler.AppBundlerTask

/**
 * Task for bundling applications into a .app for Mac OS X
 */
class AppBundleTask extends DefaultTask {
    
    AppBundleTask() {
        super()
        dependsOn("installApp")
    }

	@TaskAction
	def bundleApp() {
        
        File outputDirectory = new File(project.buildDir, "mac")
        createOutputDir(outputDirectory)
        
        AppBundlerTask appBundlerTask = new AppBundlerTask()
        appBundlerTask.setProject(project.ant.getProject())
        
        List<String> vmArgs = project.getExtensions().findByName('parcl').app.vmArgs
        if(vmArgs != null) {
            appBundlerTask.options.addAll(vmArgs)
        }
        
        List<String> appArgs = project.getExtensions().findByName('parcl').app.appArgs
        if(appArgs != null) {
            appBundlerTask.arguments.addAll(appArgs)
        }
        
        appBundlerTask.outputDirectory = outputDirectory
        appBundlerTask.name = project.getExtensions().findByName('parcl').app.appName
        appBundlerTask.shortVersion = project.getVersion().toString()
        appBundlerTask.icon = project.file(project.getExtensions().findByName('parcl').app.icon)
        appBundlerTask.copyright = project.getExtensions().findByName('parcl').app.copyright
        appBundlerTask.applicationCategory = project.getExtensions().findByName('parcl').app.applicationCategory
        appBundlerTask.displayName = project.getExtensions().findByName('parcl').app.displayName
        appBundlerTask.identifier = project.getExtensions().findByName('parcl').app.identifier
        appBundlerTask.mainClassName = project.convention.plugins.application.mainClassName
        
		String javaHome = project.getExtensions().findByName('parcl').app.javaHome
        if(javaHome != null) {
			FileSet runtimeFileSet = new FileSet()
			runtimeFileSet.setDir(new File(javaHome))
            appBundlerTask.addConfiguredRuntime(runtimeFileSet)
        }
        
        FileSet classpathFileSet = new FileSet()
        classpathFileSet.setDir(getOutputJarsDirectory())
        classpathFileSet.setIncludes("*.jar")
        appBundlerTask.addConfiguredClassPath(classpathFileSet)
        
        appBundlerTask.execute()
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
}
