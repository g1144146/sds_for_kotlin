package sds.classfile.bytecode

import sds.classfile.constant_pool.ClassInfo
import sds.classfile.constant_pool.NumberInfo
import sds.classfile.constant_pool.StringInfo
import sds.classfile.ClassfileStream as Stream
import sds.classfile.constant_pool.Constant as Cons

open class ReferenceToPool(data: Stream, pool: Array<Cons>, pc: Int, type: String): Opcode(pc, type) {
    val index: Int = if(type == "ldc") data.unsignedByte() else data.short()
    val ldcType: String = when(type) {
        "ldc", "ldc_w", "ldc2_w" -> when(pool[index - 1]) {
            is NumberInfo -> when((pool[index - 1] as NumberInfo).number) {
                is Int    -> "int"
                is Float  -> "float"
                is Long   -> "long"
                else      -> "double"
            }
            is StringInfo -> "String"
            is ClassInfo  -> extract(index, pool)
            else          -> "" // MethodHandle
        }
        else -> ""
    }
    val operand: String = if(ldcType == "String") "\"${extract(index, pool)}\"" else extract(index, pool)

    override fun toString(): String {
        val before: String = "${super.toString()}: #$index($operand"
        val after:  String = if(ldcType == "") ")" else "(${ldcType}))"
        return before + after
    }
}