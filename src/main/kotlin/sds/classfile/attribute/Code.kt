package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream
import sds.classfile.attribute.Attribute
import sds.classfile.bytecode.Opcode
import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.constant_pool.Utf8Info as Utf8

class Code(data: Stream, pool: Array<Cons>): Attribute {
    val maxStack:  Int = data.short()
    val maxLocals: Int = data.short()
    val opcodes: Array<Opcode> = initOpcode(data, pool)
    val exTable: Array<Pair<Triple<Int, Int, Int>, String>> = (0 until data.short()).map {
        // start, end, handle
        val indexes: Triple<Int, Int, Int> = Triple(data.short(), data.short(), data.short())
        val type: Int = data.short()
        val target: String = if(type == 0) "any" else extract(type, pool)
        indexes to target
    }.toTypedArray()
    val attributes: Array<Attribute> = (0 until data.short()).map {
        val utf8: Utf8 = pool[data.short() - 1] as Utf8
        Attribute.create(utf8.value, data, pool, opcodes)
    }.toTypedArray()


    private fun initOpcode(data: Stream, pool: Array<Cons>): Array<Opcode> {
        val len: Int = data.int()
        val pointer: Int = data.pointer()
        return readOpcode(len, pointer, data, pool, mutableListOf())
    }

    tailrec private fun readOpcode(len: Int, pointer: Int, data: Stream,
                                   pool: Array<Cons>, opcodes: MutableList<Opcode>): Array<Opcode> {
        val index: Int = data.pointer()
        if(len + pointer <= index) {
            return opcodes.toTypedArray()
        }
        val pc: Int = index - pointer
        opcodes.add(Opcode.create(pc, data, pool))
        return readOpcode(len, pointer, data, pool, opcodes)
    }
}