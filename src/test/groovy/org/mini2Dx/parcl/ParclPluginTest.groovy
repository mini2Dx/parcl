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

import static org.junit.Assert.*

import org.apache.tools.ant.types.FileSet
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class ParclPluginTest {
    @Test
    public void testPluginLoadsExtension() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.mini2Dx.parcl'

        String testName = "example"
        String testJarPath = "path/example.jar"
        String testMainClassName = "com.example.Example"

        String testJavaHome = "/test/app/path/java"
        String testIconPath = "icon.icns"
        String testApplicationCategory = "public.app-category.adventure-games"
        String testIdentifier = "com.example.ExampleGame"
        String testCopyright = "Copyright 2015 Test Company"
        
        def testVmArgs = ["-Xmx1g"]
        def testAppArgs = ["test1", "test2"]

        project.configure(project) {
            mainClassName = testMainClassName

            parcl {
                mainJar = testJarPath

                exe {
					vmArgs = testVmArgs
					appArgs = testAppArgs
                    exeName = testName

					withJre(testJavaHome)
                }

                app {
                    vmArgs = testVmArgs
                    appArgs = testAppArgs
                    appName = testName
                    icon = testIconPath
                    applicationCategory = testApplicationCategory
                    displayName = testName
                    identifier = testIdentifier
                    copyright = testCopyright

                    withJre(testJavaHome)
                }
				
				linux {
					vmArgs = testVmArgs
					appArgs = testAppArgs
					binName = testName

					withJre(testJavaHome)
					
					deb {
						
					}
				}
            }
        }

        assertTrue(project.getExtensions().findByName('parcl').mainJar.equals(testJarPath))

        assertTrue(project.getExtensions().findByName('parcl').exe.exeName.equals(testName))
        assertTrue(project.getExtensions().findByName('parcl').exe.javaHome.equals(testJavaHome))
		assertTrue(project.getExtensions().findByName('parcl').exe.vmArgs instanceof List)
		assertTrue(project.getExtensions().findByName('parcl').exe.appArgs instanceof List)

        assertTrue(project.getExtensions().findByName('parcl').app.appName.equals(testName))
        assertTrue(project.getExtensions().findByName('parcl').app.icon.equals(testIconPath))
        assertTrue(project.getExtensions().findByName('parcl').app.applicationCategory.equals(testApplicationCategory))
        assertTrue(project.getExtensions().findByName('parcl').app.displayName.equals(testName))
        assertTrue(project.getExtensions().findByName('parcl').app.identifier.equals(testIdentifier))
        assertTrue(project.getExtensions().findByName('parcl').app.copyright.equals(testCopyright))
        assertTrue(project.getExtensions().findByName('parcl').app.javaHome.equals(testJavaHome))
        assertTrue(project.getExtensions().findByName('parcl').app.vmArgs instanceof List)
        assertTrue(project.getExtensions().findByName('parcl').app.appArgs instanceof List)
		
		assertTrue(project.getExtensions().findByName('parcl').linux.binName.equals(testName))
		assertTrue(project.getExtensions().findByName('parcl').linux.javaHome.equals(testJavaHome))
		assertTrue(project.getExtensions().findByName('parcl').linux.vmArgs instanceof List)
		assertTrue(project.getExtensions().findByName('parcl').linux.appArgs instanceof List)
    }
}
