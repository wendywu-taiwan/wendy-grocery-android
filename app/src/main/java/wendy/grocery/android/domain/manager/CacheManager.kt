package wendy.grocery.android.domain.manager

import android.content.Context
import android.os.FileUtils
import wendy.grocery.android.application.WendyGroceryApplication
import wendy.grocery.android.utilities.FileUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Object that define operations on cache to store, retrieve and delete data
 */
object CacheManager {

    // ===========================================================
    // Constants
    // ===========================================================

    val TAG = CacheManager::class.java.simpleName

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * Default file folder name
     */
    private const val APP_FOLDER = "app"
    /**
     * Default directory where data is stored
     */
    private var appFolder: File

    /**
     * The map that store the running caching process,
     * if this map contains given file name,
     * means it is running caching process.
     *
     * key: file name
     * value: [CacheParameter]
     * */
    private var cachingFileMap: ConcurrentHashMap<String, CacheParameter> = ConcurrentHashMap()


    init {
        val applicationContext : Context = WendyGroceryApplication.getInstance().applicationContext
        appFolder = File(applicationContext.filesDir, APP_FOLDER)
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Return root folder of cache file path */
    fun getDefaultCacheFolder(): File {
        return appFolder
    }

    /**
     * Cache content in the given file path
     *
     * @param fileName name of the file
     * @param content content to write
     * @param folder the folder where to store the file
     */
    fun cache(fileName:String, content: String, folder: File = appFolder): String {
        val newCacheParameter = CacheParameter(fileName, content, folder)
        val oldCacheParameter = cachingFileMap[fileName]

        // if there is no running caching process for this file,
        // save it to cachingFileMap and run the caching process
        if(oldCacheParameter == null){
            cachingFileMap[fileName] = newCacheParameter
            return cache(newCacheParameter)
        }

        // if there is running caching process, and the content is new,
        // save it to cachingFileMap,
        // so it will run the caching process again after current process finished
        if(oldCacheParameter.content != newCacheParameter.content){
            cachingFileMap[fileName] = newCacheParameter
        }

        return content
    }

    /** Save data to file cache with given [CacheParameter] */
    private fun cache(cacheParameter: CacheParameter): String {
        val folder = cacheParameter.folder
        val fileName = cacheParameter.fileName
        val content = cacheParameter.content

        if (!folder.exists()) folder.mkdirs()

        // need to delete the file because it does not replace the existing file
        delete(fileName)
        FileUtil.saveFile(folder, fileName, content)
        val checkedCacheParameter = cachingFileMap[fileName]

        // if checkedCachedParameter is not empty and has new content,
        // means we need to save the new content to the cache
        // call cache with checkedCacheParameter
        if(checkedCacheParameter?.content != null && checkedCacheParameter.content != content) {
            return cache(checkedCacheParameter)
        }

        // remove the proceed cacheParameter from the cache file map
        cachingFileMap.remove(fileName)
        return content
    }

    /**
     * Retrieve content from file
     *
     * @param fileName name of the file
     * @param folder the folder where the file is stored
     * @return string content
     */
    fun load(fileName:String, folder: File = appFolder) : String {
        return FileUtil.loadFile(folder, fileName)
    }

    /**
     * Delete content given the file name
     *
     * @param fileName
     * @param folder the folder where the file is stored
     */
    fun delete(fileName: String, folder: File = appFolder): Boolean {
        val file = File(folder, fileName)
        return file.delete()
    }

    /**
     * Delete all the files in the given folder
     * */
    fun delete(folder: File): Boolean {
        return folder.deleteRecursively()
    }


    /** Store the values that we need for running caching process */
    data class CacheParameter(val fileName: String, val content: String, val folder: File = appFolder)
}