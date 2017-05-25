package sds.classfile.bytecode

import sds.classfile.ClassfileStream as Stream

sealed class Switch(data: Stream, pc: Int, type: String): Opcode(pc, type) {
    val default: Int
    init {
        skip(1 + pc, data)
        this.default = data.int() + pc
    }

    tailrec private fun skip(x: Int, data: Stream) {
        if((x % 4) == 0) return
        data.byte()
        skip(x + 1, data)
    }
}

class TableSwitch(data: Stream, pc: Int): Switch(data, pc, "tableswitch") {
    val offsets: IntArray
    init {
        val low:  Int = data.int()
        val high: Int = data.int()
        this.offsets = (0 until (high - low + 1)).map { data.int() + pc } .toIntArray()
    }
}

class LookupSwitch(data: Stream, pc: Int): Switch(data, pc, "lookupswitch") {
    val matchOffset: Array<Pair<Int, Int>> = (0 until data.int()).map {
        data.int() to (data.int() + pc)
    }.toTypedArray()
}