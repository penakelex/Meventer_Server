package org.penakelex.fileSystem

import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

class FileManager(private val directory: String) {
    suspend fun uploadFile(fileItems: List<PartData.FileItem>): List<String> = coroutineScope {
        async(Dispatchers.IO) {
            fileItems.map { fileItem ->
                val filePath = "$directory\\${getNewFileName()}.${
                     fileItem.originalFileName?.substringAfterLast('.')
                }"
                with(File(filePath)) {
                    writeBytes(fileItem.streamProvider().readBytes())
                    return@with name
                }
            }
        }.await()
    }

    private suspend fun getNewFileName(): String = coroutineScope {
        async(Dispatchers.Default) {
            var maximum = 0L
            for (file in File(directory).listFiles() ?: return@async "%020X".format(1)) {
                val fileName = file.name.substringBeforeLast('.').toLongOrNull(16) ?: continue
                if (fileName > maximum) maximum = fileName
            }
            return@async "%020X".format(maximum + 1)
        }.await()
    }
}