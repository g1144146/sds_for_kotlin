package sds.classfile.constant_pool

import sds.classfile.ClassfileStream

class NumberInfo(data: ClassfileStream, type: Int): Constant() {
    val number: Number = when(type) {
        Type.INT    -> data.int()
        Type.FLOAT  -> data.float()
        Type.LONG   -> data.long()
        Type.DOUBLE -> data.double()
        else -> throw IllegalStateException("unknown number type.")
    }

    override fun toString(): String = "${number.javaClass.simpleName}Info\t$number"
}