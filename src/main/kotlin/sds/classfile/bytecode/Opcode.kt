package sds.classfile.bytecode

import sds.classfile.ClassfileInformation
import sds.classfile.ClassfileStream as Stream
import sds.classfile.bytecode.MnemonicTable as Table
import sds.classfile.constant_pool.Constant as Cons
import sds.util.DescriptorParser.parse

open class Opcode(val pc: Int, val type: String): ClassfileInformation {
    override fun toString(): String = "$pc - $type"
    companion object OpcodeFactory {
        fun create(pc: Int, data: Stream, pool: Array<Cons>): Opcode {
            val opcode: Int = data.byte() and 0xff
            val _type: String = Table.types[opcode]
            return when(opcode) {
                in 0x00..0x0F, in 0x1A..0x35, in 0x3B..0x83, in 0x85..0x98,
                in 0xAC..0xB1, 0xBE,    0xBF, 0xC2,    0xC3, 0xCA -> Opcode(pc, _type)
                0x10, 0x11 -> Push(data, pc, _type)
                in 0x12..0x14, in 0xB2..0xB8, 0xBB, 0xBD, 0xC0, 0xC1 -> ReferenceToPool(data, pool, pc, _type)
                in 0x15..0x19, in 0x36..0x3A, 0xA9 -> Index(data.unsignedByte(), pc, _type)
                0x84       -> Iinc(data.unsignedByte(), data.byte(), pc)
                in 0x99..0x9F, in 0xA0..0xA8, 0xC6, 0xC7 -> Branch(data.short(), pc, _type)
                0xAA       -> TableSwitch(data, pc)
                0xAB       -> LookupSwitch(data, pc)
                0xB9       -> InvokeInterface(data, pool, pc)
                0xBA       -> InvokeDynamic(data, pool, pc)
                0xBC       -> NewArray(data.unsignedByte(), pc)
                0xC4       -> Wide(data, pool, pc)
                0xC5       -> MultiANewArray(data, pool, pc)
                0xC8, 0xC9 -> Branch(data.int(), pc, _type)
                0xFE       -> Opcode(pc, Table.types[0xCB])
                0xFF       -> Opcode(pc, Table.types[0xCC])
                else -> throw IllegalArgumentException("undefined opcode ($opcode).")
            }
        }
    }
}

class Branch(val branch: Int, pc: Int, type: String): Opcode(pc, type) {
    override fun toString(): String = "${super.toString()}: $branch"
}

class Iinc(val index: Int, val cons: Int, pc: Int): Opcode(pc, "iinc") {
    override fun toString(): String = "${super.toString()}: $index, $cons"
}

class Index(val value: Int, pc: Int, type: String): Opcode(pc, type) {
    override fun toString(): String = "${super.toString()}: $value"
}

class InvokeDynamic(data: Stream, pool: Array<Cons>, pc: Int): ReferenceToPool(data, pool, pc, "invokedynamic") {
    init { data.skip(2) }
}

class InvokeInterface(data: Stream, pool: Array<Cons>, pc: Int): ReferenceToPool(data, pool, pc, "invokeinterface") {
    val count: Int
    init {
        this.count = data.unsignedByte()
        data.skip(1)
    }
    override fun toString(): String = "${super.toString()}, $count"
}

class MultiANewArray(data: Stream, pool: Array<Cons>, pc: Int): ReferenceToPool(data, pool, pc, "multianewarray") {
    val dimensions: Int = data.byte()
    fun operand(): String = parse(super.operand)
    override fun toString(): String  = "${(this as Opcode).toString()}: #$index(${operand()}), $dimensions"
}

class NewArray(typeIndex: Int, pc: Int): Opcode(pc, "anewarray") {
    val atype: String = when(typeIndex) {
        4    -> "boolean"
        5    -> "char"
        6    -> "float"
        7    -> "double"
        8    -> "byte"
        9    -> "short"
        10   -> "int"
        11   -> "long"
        else -> throw IllegalArgumentException("unknown array type ($typeIndex).")
    }
    override fun toString(): String = "${super.toString()}: $atype"
}

class Push(data: Stream, pc: Int, type: String): Opcode(pc, type) {
    val value: Int = if(type == "bipush") data.byte() else data.short()
    override fun toString(): String = "${super.toString()}: $value"
}

class Wide(data: Stream, pool: Array<Cons>, pc: Int): Opcode(pc, "wide") {
    val tag: Int = data.byte()
    val ref: ReferenceToPool = ReferenceToPool(data, pool, pc, Table.types[tag])
    val _const: Int = if(Table.types[tag] == "iinc") data.short() else -1
}