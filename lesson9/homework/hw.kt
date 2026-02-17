import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun archiveDirectory(sourceDir : String, archivePath : String){
    val folder = File(sourceDir)

    if (!folder.exists() || !folder.isDirectory) {
        println("Error: folder '$sourceDir' doesn`t exist")
        return
    }

    FileOutputStream(archivePath).use { fos ->
        ZipOutputStream(fos).use { zos ->

            addFilesToZip(folder, folder, zos)

            println("V Archive created")
        }
    }
}

fun addFilesToZip(rootFolder: File, currentFolder: File, zipOS: ZipOutputStream) {

    currentFolder.listFiles()?.forEach { file ->

        if (file.isDirectory) {

            addFilesToZip(rootFolder, file, zipOS)
        } else {

            val fileName = file.name.lowercase()

            if (fileName.endsWith(".txt") || fileName.endsWith(".log")) {

                val relativePath = rootFolder.toPath().relativize(file.toPath()).toString()
                val zipEntry = ZipEntry(relativePath)

                FileInputStream(file).use { fileStream ->
                    zipOS.putNextEntry(zipEntry)

                    fileStream.copyTo(zipOS)

                    zipOS.closeEntry()
                    println("File added")
                }
            }
        }
    }
}