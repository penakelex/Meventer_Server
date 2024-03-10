package org.penakelex.fileSystem

import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

class FileManager(private val directory: String) {
    suspend fun uploadFile(fileItems: List<PartData.FileItem>): List<String> = coroutineScope {
        //TODO: Apply changes
        val fileNumber = getNewFileNumber()
        val fileNames = (fileNumber until fileNumber + fileItems.size).map { "%020X".format(it) }
        fileItems.mapIndexed { index, fileItem ->
            async(Dispatchers.IO) {
                val fileExtension = fileItem.originalFileName?.substringAfterLast('.')
                val filePath = "$directory\\${fileNames[index]}.${fileExtension}"
                with(File(filePath)) {
                    writeBytes(fileItem.streamProvider().readBytes())
                    return@with name
                }
            }
        }.map { it.await() }
    }

    suspend fun downloadFile(fileName: String): File = coroutineScope {
        async(Dispatchers.IO) {
            File("$directory\\$fileName")
        }.await()
    }

    private suspend fun getNewFileNumber(): Long = coroutineScope {
        async(Dispatchers.Default) {
            var maximum = 0L
            for (file in File(directory).listFiles() ?: return@async 1) {
                val fileName = file.nameWithoutExtension.toLongOrNull(16) ?: continue
                if (fileName > maximum) maximum = fileName
            }
            return@async maximum + 1
        }.await()
    }
}