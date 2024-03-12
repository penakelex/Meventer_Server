package org.penakelex.fileSystem

import io.ktor.http.content.*
import java.io.File

interface FileManager {
    /**
     * Uploads given files to file system
     * @param fileItems list of FileItems
     * @return list of file names
     * */
    suspend fun uploadFile(fileItems: List<PartData.FileItem>): List<String>

    /**
     * Downloads file by the [fileName]
     * @param fileName file name to download
     * @return file if it is found else null
     * */
    suspend fun downloadFile(fileName: String): File?

    /**
     * Checks last one maximum file and gets new name
     * @return new file name (number)
     * */
    suspend fun getNewFileNumber(): Long
}