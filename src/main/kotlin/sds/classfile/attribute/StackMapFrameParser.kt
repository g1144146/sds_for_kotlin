package sds.classfile.attribute

import kotlin.collections.HashMap as Hash
import kotlin.collections.LinkedHashMap as Linked
import kotlin.collections.ArrayList
import sds.classfile.attribute.StackMapFrame as Frame
import sds.classfile.attribute.StackMapFrame.*
import sds.classfile.attribute.VerificationType as Verify
import sds.classfile.attribute.VerificationType.*
import sds.classfile.bytecode.Opcode
import sds.classfile.bytecode.ReferenceToPool as Ref
import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.constant_pool.ConstantValueExtractor.extract
import sds.util.DescriptorParser.parse

object StackMapFrameParser {
    fun parseFrame(frames: List<Frame>, pool: Array<Cons>,
                   opcodes: Array<Opcode>): Linked<Pair<Int, Int>, Hash<String, ArrayList<String>>> {
        var before: Int = 0
        var beforeTag: Int = 0
        val parsedFrames: Linked<Pair<Int, Int>, Hash<String, ArrayList<String>>> = linkedMapOf()
        frames.forEach { frame: Frame ->
            val map: Hash<String, ArrayList<String>> = hashMapOf()
            val locals: ArrayList<String> = getBeforeLocals(parsedFrames, beforeTag to before)
            val key: Int = when(frame) {
                is SameLocals1StackItemExtended -> {
                    map.put("stack", arrayListOf(parseVerification(frame.stack, pool, opcodes)))
                    map.put("local", locals)
                    frame.offset
                }
                is SameLocals1StackItem -> {
                    map.put("stack", arrayListOf(parseVerification(frame.stack, pool, opcodes)))
                    map.put("local", locals)
                    frame.tag - 64
                }
                is Append -> {
                    locals.addAll(frame.locals.map { parseVerification(it, pool, opcodes) } )
                    map.put("stack", arrayListOf())
                    map.put("local", locals)
                    frame.offset
                }
                is Full -> {
                    map.put("stack", frame.stacks.map { parseVerification(it, pool, opcodes) } as ArrayList)
                    map.put("local", frame.locals.map { parseVerification(it, pool, opcodes) } as ArrayList)
                    frame.offset
                }
                is Chop -> {
                    val deleteArg = 251 - frame.tag
                    (0 until deleteArg).forEach { locals.removeAt(locals.size - 1) }
                    map.put("stack", arrayListOf())
                    map.put("local", locals)
                    frame.tag
                }
                else -> {
                    map.put("stack", arrayListOf())
                    map.put("local", locals)
                    (frame as Same).tag
                }
            }
            parsedFrames.put(((frame as Same).tag to before), map)
            before = key
            beforeTag = (frame as Same).tag
        }
        return parsedFrames
    }

    private fun getBeforeLocals(parsedFrames: Linked<Pair<Int, Int>, Hash<String, ArrayList<String>>>,
                                before: Pair<Int, Int>): ArrayList<String> {
        return parsedFrames.getOrDefault(before,  linkedMapOf())
                           .getOrDefault("local", arrayListOf()).clone() as ArrayList<String>
    }

    private fun parseVerification(v: Verify, pool: Array<Cons>, opcodes: Array<Opcode>): String = when(v) {
        is ObjectVar -> {
            val value: String = extract(v.cpool, pool)
            if(value.first() == '[') parse(value) else value
        }
        is UninitializedVar -> (opcodes[v.offset] as Ref).operand
        else                -> v.toString()
    }
}