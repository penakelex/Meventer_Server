package org.penakelex.fileSystem

import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.atomic.AtomicLong

private const val File_Name_Pattern = "%020X"

/**
 * File manager implementation
 * */
class FileManagerImplementation(private val directory: String) : FileManager {
    private val newFileNumber = AtomicLong(getNewFileNumber())

    override suspend fun uploadFiles(fileItems: List<PartData.FileItem>): List<String> = coroutineScope {
        val fileNames = List(fileItems.size) { File_Name_Pattern.format(newFileNumber.getAndIncrement()) }
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

    override suspend fun uploadFile(fileBytes: ByteArray): String? = coroutineScope {
        async(Dispatchers.IO) {
            try {
                with(File(File_Name_Pattern.format(newFileNumber.getAndIncrement()))) {
                    writeBytes(fileBytes)
                    return@async name
                }
            } catch (exception: Exception) {
                null
            }
        }.await()
    }

    override suspend fun downloadFile(fileName: String): File? = coroutineScope {
        async(Dispatchers.IO) {
            try {
                File("$directory\\$fileName")
            } catch (exception: FileNotFoundException) {
                null
            }
        }.await()
    }

    /**
     * Checks last one maximum file and gets new name
     * @return new file name (number)
     * */
    private fun getNewFileNumber(): Long {
        val files = File(directory).listFiles() ?: return 1
        return files.maxOf { file ->
            file.nameWithoutExtension.toLongOrNull(16) ?: Long.MIN_VALUE
        } + 1
    }
}