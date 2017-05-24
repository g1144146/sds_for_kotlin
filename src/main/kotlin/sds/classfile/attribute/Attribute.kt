package sds.classfile.attribute

import sds.classfile.ClassfileInformation
import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

open class Attribute: ClassfileInformation {
    override fun toString(): String = this.javaClass.simpleName

    companion object AttributeFactory {
        fun create(name: String, data: ClassfileStream, pool: Array<Constant>): Attribute {
            val len: Int = data.int()
            return when(name) {
                "ConstantValue" -> ConstantValue(data, pool)
                "Deprecated"    -> Deprecated()
                "Signature"     -> Signature(data, pool)
                "SourceFile"    -> SourceFile(data, pool)
                "Synthetic"     -> Synthetic()
                else -> throw IllegalArgumentException("unknown attribute. ($name)")
            }
        }
    }
}

class Deprecated: Attribute()
class Synthetic: Attribute()