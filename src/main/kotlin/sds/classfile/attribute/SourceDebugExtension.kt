package sds.classfile.attribute

import sds.classfile.ClassfileStream

class SourceDebugExtension(data: ClassfileStream, len: Int): Attribute {
    val debug: IntArray = (0 until len).map { data.unsignedByte() } .toIntArray()
    override fun toString(): String = "[SourceDebugExtension]: [${debug.joinToString(", ")}]"
}