package sds.classfile.attribute

import sds.classfile.ClassfileInformation
import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

interface Attribute: ClassfileInformation {
    companion object AttributeFactory {
        fun create(name: String, data: ClassfileStream, pool: Array<Constant>): Attribute {
            val len: Int = data.int()
            return when {
                name.startsWith("LocalVariable")      -> LocalVariable(name, data, pool)
                name.endsWith("ParameterAnnotations") -> RuntimeParameterAnnotations(name, data, pool)
                name.endsWith("TypeAnnotations")      -> RuntimeTypeAnnotations(name, data, pool)
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
    }
}

class Deprecated: Attribute {
    override fun toString(): String = "[Deprecated]"
}
class Synthetic: Attribute {
    override fun toString(): String = "[Synthetic]"
}