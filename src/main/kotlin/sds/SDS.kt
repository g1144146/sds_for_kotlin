package sds

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.collections.MutableList

class SDS(args: Array<String>) {
    private var jarfiles: MutableList<JarFile> = mutableListOf()
    private val classfiles: MutableList<String> = mutableListOf()


    init {
        args.forEach { parseArgs(it) }
    }

    private fun parseArgs(arg: String) {
        when {
            arg.endsWith(".jar")   -> jarfiles.add(JarFile(File(arg)))
            arg.endsWith(".class") -> classfiles.add(arg)
            else -> throw IllegalArgumentException("invalid file.")
        }
    }

    fun run() {
        if(classfiles.isNotEmpty()) {
            classfiles.forEach { file: String ->
                val reader: ClassfileReader = ClassfileReader(file)
            }
        }
        // not execute if jar is null
        jarfiles.forEach { jar : JarFile ->
            jar.stream().forEach { entry: JarEntry ->
                val file: String = entry.toString()
                if(file.endsWith(".class")) {
                    // throws Exception if jar is null
                    val reader: ClassfileReader = ClassfileReader(jar.getInputStream(entry))
                }
            }
        }
    }
}