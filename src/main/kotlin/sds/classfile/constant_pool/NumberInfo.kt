package sds.classfile.constant_pool

import sds.classfile.ClassfileStream

class NumberInfo(data: ClassfileStream, type: Int): Constant(type) {
    val number: Number = when(tag) {
        Type.INT    -> data.int()
        Type.FLOAT  -> data.float()
        Type.LONG   -> data.long()
        Type.DOUBLE -> data.double()
        else -> throw IllegalStateException("unknown number type.")
    }

    override fun toString(): String = super.toString() + "\t" + number.toString()
}