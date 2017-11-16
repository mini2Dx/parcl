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
package org.mini2Dx.parcl

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.tasks.bundling.Zip

import org.mini2Dx.parcl.domain.App
import org.mini2Dx.parcl.domain.Linux
import org.mini2Dx.parcl.domain.Deb
import org.mini2Dx.parcl.domain.Exe
import org.mini2Dx.parcl.task.AppBundleTask
import org.mini2Dx.parcl.task.LinuxBundleTask
import org.mini2Dx.parcl.task.ExeBundleTask

/**
 * Implements the plugin. Applies configuration extensions and tasks to a project.
 */
class ParclPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.plugins.apply ApplicationPlugin
		
		project.extensions.create("parcl", ParclExtension)
		project.parcl.extensions.create("exe", Exe)
		project.parcl.extensions.create("app", App)
		project.parcl.extensions.create("linux", Linux)
		project.parcl.linux.extensions.create("deb", Deb)

		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			project.task('bundleNative', type: ExeBundleTask)
			project.tasks.create([
				name: 'bundleNativeZip',
				type: Zip, 
				dependsOn: 'bundleNative'
			]) {
				from "${project.buildDir}/windows/"
				include '**/*'
				conventionMapping.archiveName = {
					project.getExtensions().findByName('parcl').exe.exeName + ".zip"
				}
				destinationDir(project.file('build'))
			}
		} else if (Os.isFamily(Os.FAMILY_MAC)) {
			project.task('bundleNative', type: AppBundleTask)
			project.tasks.create([
				name: 'bundleNativeZip',
				type: Zip, 
				dependsOn: 'bundleNative'
			]) {
				from "${project.buildDir}/mac/"
				include '**/*'
				conventionMapping.archiveName = {
					project.getExtensions().findByName('parcl').app.appName + ".zip"
				}
				destinationDir(project.file('build'))
			}
		} else if (Os.isFamily(Os.FAMILY_UNIX)) {
			project.task('bundleNative', type: LinuxBundleTask)
			project.tasks.create([
				name: 'bundleNativeZip',
				type: Zip,
				dependsOn: 'bundleNative'
			]) {
				from "${project.buildDir}/linux/"
				include '**/*'
				conventionMapping.archiveName = {
					project.getExtensions().findByName('parcl').linux.binName + ".zip"
				}
				destinationDir(project.file('build'))
			}
		}
	}
}
