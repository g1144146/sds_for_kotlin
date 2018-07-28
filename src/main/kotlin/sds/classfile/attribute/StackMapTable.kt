package sds.classfile.attribute

import kotlin.collections.HashMap as Hash
import kotlin.collections.LinkedHashMap as Linked
import kotlin.collections.ArrayList
import sds.classfile.ClassfileStream as Stream
import sds.classfile.attribute.VerificationType as Verify
import sds.classfile.attribute.StackMapFrameParser.parseFrame
import sds.classfile.bytecode.Opcode
import sds.classfile.constant_pool.Constant as Cons

class StackMapTable(data: Stream, pool: Array<Cons>, opcodes: Array<Opcode>): Attribute() {
    val entries: Linked<Pair<Int, Int>, Hash<String, ArrayList<String>>> = parseFrame(
        (0 until data.short()).map { StackMapFrame.create(data) }, pool, opcodes
    )
}

interface StackMapFrame {
    companion object StackMapFactory {
        fun create(data: Stream): StackMapFrame {
            val tag: Int = data.unsignedByte()
            return when(tag) {
                in 0..63    -> Same(tag)
                in 64..127  -> SameLocals1StackItem(data, tag)
                247         -> SameLocals1StackItemExtended(data.short(), data, tag)
                in 248..250 -> Chop(data.short(), tag)
                251         -> SameExtended(data, tag)
                in 252..254 -> Append(data, tag)
                255         -> Full(data, tag)
                else -> throw IllegalArgumentException("unknown tag ($tag).")
            }
        }
    }

    open class Same(val tag: Int): StackMapFrame
    open class SameLocals1StackItem(data: Stream, tag: Int): Same(tag) {
        val stack: Verify = Verify.create(data)
    }
    class SameLocals1StackItemExtended(val offset: Int, data: Stream, tag: Int): SameLocals1StackItem(data, tag)
    open class Chop(val offset: Int, tag: Int): Same(tag)
    class SameExtended(data: Stream, tag: Int): Chop(data.short(), tag)
    class Append(data: Stream, tag: Int): Chop(data.short(), tag) {
        val locals: Array<Verify> = (0 until (tag - 251)).map { Verify.create(data) } .toTypedArray()
    }
    class Full(data: Stream, tag: Int): Chop(data.short(), tag) {
        val locals: Array<Verify> = (0 until data.short()).map { Verify.create(data) } .toTypedArray()
        val stacks: Array<Verify> = (0 until data.short()).map { Verify.create(data) } .toTypedArray()
    }
}