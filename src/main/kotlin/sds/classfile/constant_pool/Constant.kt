package sds.classfile.constant_pool

import sds.classfile.Information
import sds.classfile.ClassfileStream

open class Constant: Information {
    companion object ConstantFactory {
        fun create(data: ClassfileStream): Constant {
            val _tag: Int = data.byte()
            return when(_tag) {
                Type.INT,   Type.FLOAT,  Type.LONG, Type.DOUBLE -> NumberInfo(data, _tag)
                Type.FIELD, Type.METHOD, Type.INTERFACE         -> MemberInfo(_tag, data.short(), data.short())
                Type.UTF8           -> Utf8Info(String(data.fully(data.short())))
                Type.CLASS          -> ClassInfo(data.short())
                Type.STRING         -> StringInfo(data.short())
                Type.NAME_AND_TYPE  -> NameAndTypeInfo(data.short(), data.short())
                Type.HANDLE         -> HandleInfo(data.byte(), data.short())
                Type.TYPE           -> TypeInfo(data.short())
                Type.INVOKE_DYNAMIC -> InvokeDynamicInfo(data.short(), data.short())
                else                -> ConstantAdapter()
            }
        }
    }
}

class ClassInfo(val index: Int): Constant() {
    override fun toString(): String = "Class\t#$index"
}

class InvokeDynamicInfo(val bsmAtt: Int, val nameAndType: Int): Constant() {
    override fun toString(): String = "InvokeDynamic\t#$bsmAtt:#$nameAndType"
}

class NameAndTypeInfo(val name: Int, val type: Int): Constant() {
    override fun toString(): String = "NameAndType\t#$name:#$type"
}

class StringInfo(val string: Int): Constant() {
    override fun toString(): String = "String\t#$string"
}

class TypeInfo(val desc: Int): Constant() {
    override fun toString(): String = "MethodType#$desc"
}

class Utf8Info(val value: String): Constant() {
    override fun toString(): String = "Utf8\t$value"
}

class ConstantAdapter: Constant() {
    override fun toString(): String = "null"
}

object Type {
    const val UTF8:           Int = 1
    const val INT:            Int = 3
    const val FLOAT:          Int = 4
    const val LONG:           Int = 5
    const val DOUBLE:         Int = 6
    const val CLASS:          Int = 7
    const val STRING:         Int = 8
    const val FIELD:          Int = 9
    const val METHOD:         Int = 10
    const val INTERFACE:      Int = 11
    const val NAME_AND_TYPE:  Int = 12
    const val HANDLE:         Int = 15
    const val TYPE:           Int = 16
    const val INVOKE_DYNAMIC: Int = 18
}