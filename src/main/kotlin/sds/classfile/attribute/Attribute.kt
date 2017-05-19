package sds.classfile.attribute

import sds.classfile.Information
import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

open class Attribute: Information {

    override fun toString(): String = this.javaClass.simpleName

    companion object AttributeFactory {
        fun create(name: String, data: ClassfileStream, pool: Array<Constant>): Attribute {
            val len: Int = data.int()
            return when(name) {
                "ConstantValue" -> ConstantValue(data.short(), pool)
                "Signature"     -> Signature(data.short(), pool)
                else -> throw IllegalArgumentException("unknown attribute. ($name)")
            }
        }
    }
}