package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class SourceFile(data: ClassfileStream, pool: Array<Constant>): Attribute() {
    val source: String = extract(data.short(), pool)
    override fun toString(): String = "${super.toString()}: $source"
}