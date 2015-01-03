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
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;

namespace parcl
{
    /// <summary>
    /// Represents the launch configuration for the Java app
    /// </summary>
    [XmlRoot("application")]
    public class JavaApplicationConfig
    {
        [XmlElement("mainClassName")]
        public String MainClassName { get; set; }

        [XmlArray("classpath")]
        [XmlArrayItem("jar")]
        public List<String> ClassPath { get; set; }

        [XmlArray(ElementName = "vmArgs")]
        [XmlArrayItem("arg")]
        public List<String> VmArgs { get; set; }

        [XmlArray(ElementName = "appArgs")]
        [XmlArrayItem("arg")]
        public List<String> AppArgs { get; set; }

        [XmlElement("includesJre")]
        public bool IncludesJre { get; set; }

        public String ToStartInfoArguments()
        {
            StringBuilder arguments = new StringBuilder();
            if (VmArgs != null)
            {
                foreach (String arg in VmArgs)
                {
                    arguments.Append(arg + " ");
                }
            }

            arguments.Append("-classpath \"");
            for (int i = 0; i < ClassPath.Count; i++)
            {
                String jar = Path.GetFullPath(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, Path.Combine("libs", ClassPath[i])));
                arguments.Append(jar);

                if(i < ClassPath.Count - 1) {
                    arguments.Append(";");
                }
            }

            arguments.Append("\" " + MainClassName);

            if (AppArgs != null)
            {
                foreach (String arg in AppArgs)
                {
                    arguments.Append(" " + arg);
                }
            }

            return arguments.ToString();
        }
    }
}
