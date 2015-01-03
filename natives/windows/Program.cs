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
using System.Diagnostics;
using System.IO;
using System.Xml.Serialization;

namespace parcl
{
    /// <summary>
    /// Main class. Loads configuration from application.xml, executes the Java app and waits for the app to exit
    /// </summary>
    public class Program
    {
        static void Main(string[] args)
        {
            if (args.Length > 0)
            {
                JavaApplicationConfig exampleConfig = new JavaApplicationConfig();
                exampleConfig.ClassPath = new List<String>();
                exampleConfig.ClassPath.Add("example.jar");
                exampleConfig.MainClassName = "com.example.Example";
                exampleConfig.IncludesJre = false;

                XmlSerializer xmlWriter = new XmlSerializer(exampleConfig.GetType());
                using (FileStream writer = File.OpenWrite(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "application.example.xml")))
                {
                    xmlWriter.Serialize(writer, exampleConfig);
                }
                return;
            }
            XmlSerializer serializer = new XmlSerializer(typeof(JavaApplicationConfig));
            JavaApplicationConfig applicationConfig = (JavaApplicationConfig) serializer.Deserialize(
                File.OpenRead(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "application.xml")));

            Process process = new Process();
            if (applicationConfig.IncludesJre)
            {
                process.StartInfo.FileName = Path.Combine("jre", Path.Combine("bin", "java"));
            }
            else
            {
                process.StartInfo.FileName = "java";
            }
            process.StartInfo.Arguments = applicationConfig.ToStartInfoArguments();
            process.StartInfo.WorkingDirectory = @AppDomain.CurrentDomain.BaseDirectory;
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.CreateNoWindow = true;

            Console.WriteLine("Executing " + Path.Combine(process.StartInfo.WorkingDirectory, process.StartInfo.FileName) + " " + process.StartInfo.Arguments);
            process.Start();
            process.WaitForExit();
        }
    }
}
