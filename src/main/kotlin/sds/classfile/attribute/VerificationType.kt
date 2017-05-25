package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream

interface VerificationType {
    companion object VerificationTypeFactory {
        fun create(data: Stream): VerificationType = when(data.unsignedByte()) {
            0    -> VerificationAdapter("top")
            1    -> VerificationAdapter("int")
            2    -> VerificationAdapter("float")
            3    -> VerificationAdapter("double")
            4    -> VerificationAdapter("long")
            5    -> VerificationAdapter("null")
            6    -> VerificationAdapter("")
            7    -> ObjectVar(data.short())
            else -> UninitializedVar(data.short())
        }
    }

    class VerificationAdapter(val type: String): VerificationType {
        override fun toString(): String = type
    }
    class ObjectVar(val cpool: Int): VerificationType
    class UninitializedVar(val offset: Int): VerificationType
}