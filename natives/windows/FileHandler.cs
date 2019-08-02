using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace parcl
{
    public class FileHandler
    {
        private string directory = AppDomain.CurrentDomain.BaseDirectory;

        public FileHandler()
        {
            PrepareDirectory();
        }

        /*
        Returns the path to a file in the chosen writable directory, when
        supplied with its name.
         */
        public string PathTo(string fileName)
        {
            return Path.Combine(directory, fileName);
        }

        /*
        Prepares a directory for use by the program. In most cases, this will
        be the folder containing the .exe; however, a folder with the name of
        the .exe in AppData\Roaming will be used if the program lacks write
        permissions to its folder.
         */
        private void PrepareDirectory()
        {
            if (!HasWritePermissions(directory))
            {
                string appData = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
                string applicationName = System.Diagnostics.Process.GetCurrentProcess().ProcessName;

                directory = Path.Combine(appData, applicationName);
            }
            Directory.CreateDirectory(directory);
        }

        /*
        Checks if the program can write to a directory by attempting to create and
        immediately delete a file named "test.log".

        Returns whether the program is able to write to the specified folder.
        */
        private static bool HasWritePermissions(string folder)
        {
            try
            {
                using (FileStream stream = File.Create(
                        Path.Combine(folder, "test.log"), 1, FileOptions.DeleteOnClose)
                    ) { };
            }
            catch
            {
                return false;
            }
            return true;
        }
    }
}
