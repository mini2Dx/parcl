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
package com.battlebardgames.parcl

import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class ParclPluginTest {
	@Test
	public void testPluginLoadsExtension() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.battlebardgames.parcl'

		String testName = "example"
		String testJarPath = "path/example.jar"
		String testMainClassName = "com.example.Example"

		String testJrePath = "/test/app/path/jdk"
		
		project.configure(project) {
			mainClassName = testMainClassName
			
			parcl {
				name = testName
				jar = testJarPath
				
				exe {
					jrePath = testJrePath
				}
			}
		}

		assertTrue(project.getExtensions().findByName('parcl').name.equals(testName))
		assertTrue(project.getExtensions().findByName('parcl').jar.equals(testJarPath))
		
		assertTrue(project.getExtensions().findByName('parcl').exe.jrePath.equals(testJrePath))
	}
}
