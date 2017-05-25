package sds.classfile.attribute

import sds.classfile.ClassfileInformation
import sds.classfile.ClassfileStream as Stream
import sds.classfile.bytecode.Opcode
import sds.classfile.constant_pool.Constant as Cons

interface Attribute: ClassfileInformation {
    companion object AttributeFactory {
        fun create(name: String, data: Stream, pool: Array<Cons>): Attribute {
            val len: Int = data.int()
            return when {
                name.startsWith("LocalVariable")      -> LocalVariable(name, data, pool)
                name.endsWith("ParameterAnnotations") -> RuntimeParameterAnnotations(name, data, pool)
                name.endsWith("TypeAnnotations")      -> RuntimeTypeAnnotations(name, data)
                name.endsWith("Annotations")          -> RuntimeAnnotations(name, data, pool)
                else -> when(name) {
                    "AnnotationDefault"    -> AnnotationDefault(data, pool)
                    "BootstrapMethods"     -> BootstrapMethods(data, pool)
                    "Code"                 -> Code(data, pool)
                    "ConstantValue"        -> ConstantValue(data, pool)
                    "Deprecated"           -> Deprecated()
                    "EnclosingMethod"      -> EnclosingMethod(data, pool)
                    "Exceptions"           -> Exceptions(data, pool)
                    "InnerClasses"         -> InnerClasses(data, pool)
                    "LineNumberTable"      -> LineNumberTable(data, pool)
                    "MethodParameters"     -> MethodParameters(data, pool)
                    "Signature"            -> Signature(data, pool)
                    "SourceDebugExtension" -> SourceDebugExtension(data, len)
                    "SourceFile"           -> SourceFile(data, pool)
                    "Synthetic"            -> Synthetic()
                    else                   -> throw IllegalArgumentException("unknown attribute. ($name)")
                }
            }
        }

        fun create(name: String, data: Stream, pool: Array<Cons>, opcodes: Array<Opcode>): Attribute {
            return if(name == "StackMapTable") {
                data.int()
                StackMapTable(data, pool, opcodes)
            } else create(name, data, pool)
        }
    }
}

class Deprecated: Attribute {
    override fun toString(): String = "[Deprecated]"
}
class Synthetic: Attribute {
    override fun toString(): String = "[Synthetic]"
}