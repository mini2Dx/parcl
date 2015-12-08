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
package org.mini2Dx.parcl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Utility functions used by multiple classes
 */
public class ParclUtils {
	/**
	 * Determines the JRE relative to the JAVA_HOME directory
	 * 
	 * @param javaHome
	 * @return Null if no JRE directory is found
	 */
	public static File getJreFolder(String javaHome) {
		File javaHomeDirectory = new File(javaHome);
		if (javaHome.contains("jre")) {
			return javaHomeDirectory;
		}

		File[] children = javaHomeDirectory.listFiles();
		for (int i = 0; i < children.length; i++) {
			File child = children[i];
			if (child.isDirectory()) {
				if (child.getAbsolutePath().contains("jre")) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * Copies a file internal to the Parcl jar to an external location
	 * 
	 * @param internalFilepath
	 *            The internal file
	 * @param outputFilepath
	 *            The path + filename to copy to
	 */
	public static void copyInternalFileToExternal(String internalFilepath,
			String outputFilepath) {
		InputStream internalStream = ParclUtils.class.getClassLoader()
				.getResourceAsStream(internalFilepath);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outputFilepath);
			byte[] buffer = new byte[2048];
			int readResult = internalStream.read(buffer);
			while (readResult != -1) {
				outputStream.write(buffer, 0, readResult);
				readResult = internalStream.read(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
