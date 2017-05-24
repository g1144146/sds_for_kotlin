package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class LineNumberTable(data: ClassfileStream, pool: Array<Constant>): Attribute {
    val table: Array<Array<Int>> = (0 until data.short()).map { arrayOf(data.short(), -1, data.short()) }.toTypedArray()
    init {
        val len = table.size
        (0 until len).forEach {
            if(it == len - 1) {
                table[it][1] = table[it][0]
                if(len > 1) table[it - 1][1] = table[it][0]
            } else if(it > 0) {
                table[it - 1][1] = table[it][0]
            }
        }
    }
}