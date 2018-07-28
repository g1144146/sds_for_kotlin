package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class Exceptions(data: ClassfileStream, pool: Array<Constant>): Attribute() {
    val exceptions: Array<String> = (0 until data.short()).map { extract(data.short(), pool) } .toTypedArray()
    override fun toString(): String = "[Exceptions]: ${exceptions.joinToString(", ")}"
}