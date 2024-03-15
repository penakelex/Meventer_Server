package org.penakelex.fileSystem

import io.ktor.http.content.*
import java.io.File

interface FileManager {
    /**
     * Uploads given files to file system
     * @param fileItems list of FileItems
     * @return list of file names
     * */
    suspend fun uploadFiles(fileItems: List<PartData.FileItem>): List<String>
    suspend fun uploadFile(fileBytes: ByteArray): String?

    /**
     * Downloads file by the [fileName]
     * @param fileName file name to download
     * @return file if it is found else null
     * */
    suspend fun downloadFile(fileName: String): File?

}