package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class ConstantValue(data: ClassfileStream, pool: Array<Constant>): Attribute {
    val value: String = extract(data.short(), pool)
    override fun toString(): String = "[ConstantValue]: $value"
}