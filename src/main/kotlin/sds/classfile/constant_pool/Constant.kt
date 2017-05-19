package sds.classfile.constant_pool

import sds.classfile.Information
import sds.classfile.ClassfileStream

open class Constant(val tag: Int): Information {
    override fun toString(): String = Type.get(tag)

    companion object ConstantFactory {
        fun create(data: ClassfileStream): Constant = when(data.byte()) {
            Type.UTF8           -> Utf8Info(String(data.fully(data.short())))
            Type.INT            -> IntInfo(data.int())
            Type.FLOAT          -> FloatInfo(data.float())
            Type.LONG           -> LongInfo(data.long())
            Type.DOUBLE         -> DoubleInfo(data.double())
            Type.CLASS          -> ClassInfo(data.short())
            Type.STRING         -> StringInfo(data.short())
            Type.FIELD          -> MemberInfo(Type.FIELD,     data.short(), data.short())
            Type.METHOD         -> MemberInfo(Type.METHOD,    data.short(), data.short())
            Type.INTERFACE      -> MemberInfo(Type.INTERFACE, data.short(), data.short())
            Type.NAME_AND_TYPE  -> NameAndTypeInfo(data.short(), data.short())
            Type.HANDLE         -> HandleInfo(data.byte(), data.short())
            Type.TYPE           -> TypeInfo(data.short())
            Type.INVOKE_DYNAMIC -> InvokeDynamicInfo(data.short(), data.short())
            else                -> ConstantAdapter(-1)
        }
    }
}

object Type {
    val UTF8:           Int = 1
    val INT:            Int = 3
    val FLOAT:          Int = 4
    val LONG:           Int = 5
    val DOUBLE:         Int = 6
    val CLASS:          Int = 7
    val STRING:         Int = 8
    val FIELD:          Int = 9
    val METHOD:         Int = 10
    val INTERFACE:      Int = 11
    val NAME_AND_TYPE:  Int = 12
    val HANDLE:         Int = 15
    val TYPE:           Int = 16
    val INVOKE_DYNAMIC: Int = 18

    fun get(tag: Int): String = when(tag) {
        UTF8           -> "Utf8"
        INT            -> "Int"
        FLOAT          -> "Float"
        LONG           -> "Long"
        DOUBLE         -> "Double"
        CLASS          -> "Class"
        STRING         -> "String"
        FIELD          -> "Field_ref"
        METHOD         -> "Mehtod_ref"
        INTERFACE      -> "InterfaceMethod_ref"
        NAME_AND_TYPE  -> "NameAndType"
        HANDLE         -> "MethodHandle"
        TYPE           -> "MethodType"
        INVOKE_DYNAMIC -> "InvokeDynamic"
        else           -> "Unknown"
    }
}