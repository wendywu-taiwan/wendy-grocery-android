package wendy.grocery.android.utilities

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.IOException

class FileUtil {
    companion object {
        val TAG = FileUtil::class.java.simpleName

        /**
         * save content to the specified file
         *
         * @param directory directory where the file is stored
         * @param fileName file in which the content is saved
         * @param content the content to save
         */
        fun saveFile(directory: File, fileName: String, content: String) {
            val file = File(directory, fileName)
            try {
                val outputStream = file.outputStream()
                outputStream.bufferedWriter().use { out -> out.write(content) }
                outputStream.close()
            } catch (e: IOException) {
                Log.w(TAG, "saveFile error:${e.message} file:${file.name}")
            }
        }

        /**
         * Load content stored in the specified file
         *
         * @param directory directory where the file is store
         * @param fileName file in which the content is saved
         * @return the String content
         */
        fun loadFile(directory: File, fileName: String):String {
            val fileToRead = File(directory, fileName)
            if (!fileToRead.exists()) {
                Log.w(TAG, "loadFile, File ${fileToRead.absolutePath} does not exist")
                return ""
            }

            return try {
                val inputStream = fileToRead.inputStream()
                val contents = inputStream.bufferedReader().use(BufferedReader::readText)
                inputStream.close()
                contents
            } catch (e : IOException) {
                Log.w(TAG, "sloadFile aveFile error:${e.message} file:${fileToRead.name}")
                ""
            }
        }
    }
}