package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant
import sds.util.AccessFlag.get

class MethodParameters(data: ClassfileStream, pool: Array<Constant>): Attribute {
    val params: Array<Array<String>> = (0 until data.short()).map {
        arrayOf(extract(data.short(), pool), get(data.short(), "nested"))
    }.toTypedArray()
    override fun toString(): String = "[MethodParameters]: [${params.map { it[0] + it[1] } .joinToString(", ")}]"
}