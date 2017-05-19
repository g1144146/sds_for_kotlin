package sds.classfile.constant_pool

import sds.classfile.Information
import sds.classfile.ClassfileStream

open class Constant(val tag: Int): Information {
    override fun toString(): String = Type.get(tag)

    companion object ConstantFactory {
        fun create(data: ClassfileStream): Constant {
            val _tag: Int = data.byte()
            return when (_tag) {
                Type.UTF8           -> Utf8Info(String(data.fully(data.short())))
                Type.INT, Type.FLOAT, Type.LONG, Type.DOUBLE ->
                                       NumberInfo(data, _tag)
                Type.CLASS          -> ClassInfo(data.short())
                Type.STRING         -> StringInfo(data.short())
                Type.FIELD, Type.METHOD, Type.INTERFACE ->
                                       MemberInfo(_tag, data.short(), data.short())
                Type.NAME_AND_TYPE  -> NameAndTypeInfo(data.short(), data.short())
                Type.HANDLE         -> HandleInfo(data.byte(), data.short())
                Type.TYPE           -> TypeInfo(data.short())
                Type.INVOKE_DYNAMIC -> InvokeDynamicInfo(data.short(), data.short())
                else                -> ConstantAdapter(-1)
            }
        }
    }
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

    fun get(tag: Int): String = when(tag) {
        UTF8           -> "Utf8"
        INT            -> "Int"
        FLOAT          -> "Float"
        LONG           -> "Long"
        DOUBLE         -> "Double"
        CLASS          -> "Class"
        STRING         -> "String"
        FIELD          -> "Field_ref"
        METHOD         -> "Method_ref"
        INTERFACE      -> "InterfaceMethod_ref"
        NAME_AND_TYPE  -> "NameAndType"
        HANDLE         -> "MethodHandle"
        TYPE           -> "MethodType"
        INVOKE_DYNAMIC -> "InvokeDynamic"
        else           -> "Unknown"
    }
}