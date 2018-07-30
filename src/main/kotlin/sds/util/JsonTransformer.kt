package sds.util

import java.util.Arrays;
import sds.Classfile
import sds.classfile.Member
import sds.classfile.attribute.*
import sds.classfile.bytecode.*
import sds.classfile.constant_pool.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * This class for transforming classfile to data of json format.
 */
object JsonTransformer {

    /**
     * return string of classfile which is transformed to json format.
     *
     * @param json classfile of json format
     * @return string of classfile which is transformed to json format
     */
    fun toJsonString(json: LinkedHashMap<String, Any>): String {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(json)
    }

    /**
     * returns classfile of json format.
     *
     * @param classfile classfile data
     * @return classfile of json format
     */
    fun transform(classfile: Classfile): LinkedHashMap<String, Any> {
        val result: LinkedHashMap<String, Any> = linkedMapOf()

        result["magic"]         = classfile.magic
        result["major"]         = classfile.major
        result["minor"]         = classfile.minor
        result["access"]        = classfile.access
        result["this"]          = classfile._this.replace("/", ".")
        result["super"]         = classfile._super
        result["interfaces"]    = classfile.interfaces
        result["constant_pool"] = transformConstantPool(classfile.pool)
        result["fields"]        = transformMembers(classfile.fields)
        result["methods"]       = transformMembers(classfile.methods)
        result["attributes"]    = transformAttributes(classfile.attributes)

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        println(gson.toJson(result))

        return result
    }

    private fun transformConstantPool(pool: Array<Constant>): LinkedHashMap<Int, Any> {
        val map: LinkedHashMap<Int, Any> = linkedMapOf()
        pool.forEachIndexed({ index, constant ->
            map.plusAssign(index to when(constant) {
                is Utf8Info -> {
                    val utf8 = hashMapOf<String, Any>()
                    utf8.plusAssign("name"  to "Utf8")
                    utf8.plusAssign("value" to constant.value)
                    utf8
                }
                is NumberInfo -> {
                    val number = hashMapOf<String, Any>()
                    number.plusAssign("name"  to constant.number.javaClass.name)
                    number.plusAssign("value" to constant.number)
                    number
                }
                is ClassInfo -> {
                    val cls = hashMapOf<String, Any>()
                    cls.plusAssign("name"  to "Class")
                    cls.plusAssign("index" to ("#" + constant.index.toString()))
                    cls
                }
                is StringInfo -> {
                    val string = hashMapOf<String, Any>()
                    string.plusAssign("name"   to "String")
                    string.plusAssign("string" to ("#" + constant.string.toString()))
                    string
                }
                is MemberInfo -> {
                    val member = hashMapOf<String, Any>()
                    member.plusAssign("name" to when(constant.type) {
                        Type.FIELD  -> "Field_ref"
                        Type.METHOD -> "Method_ref"
                        else -> "InterfaceMethod_ref"
                    })
                    member.plusAssign("class"         to ("#" + constant._class.toString()))
                    member.plusAssign("name_and_type" to ("#" + constant.nameAndType.toString()))
                    member
                }
                is NameAndTypeInfo -> {
                    val nat = hashMapOf<String, Any>()
                    nat.plusAssign("name" to "NameAndType")
                    nat.plusAssign("name" to ("#" + constant.name.toString()))
                    nat.plusAssign("type" to ("#" + constant.type.toString()))
                    nat
                }
                is HandleInfo -> {
                    val handle = hashMapOf<String, Any>()
                    handle.plusAssign("name" to "MethodHandle")
                    handle.plusAssign("kind" to when(constant.kind) {
                        1    -> "REF_getField"
                        2    -> "REF_getStatic"
                        3    -> "REF_putField"
                        4    -> "REF_putStatic"
                        5    -> "REF_invokeVirtual"
                        6    -> "REF_invokeStatic"
                        7    -> "REF_invokeSpecial"
                        8    -> "REF_newInvokeSpecial"
                        else -> "REF_invokeInterface"
                    })
                    handle.plusAssign("index" to ("#" + constant.index.toString()))
                    handle
                }
                is TypeInfo -> {
                    val type = hashMapOf<String, Any>()
                    type.plusAssign("name" to "MethodType")
                    type.plusAssign("desc" to ("#" + constant.desc.toString()))
                    type
                }
                is InvokeDynamicInfo -> {
                    val indy = hashMapOf<String, Any>()
                    indy.plusAssign("name"          to "InvokeDynamic")
                    indy.plusAssign("bsm_att"       to ("#" + constant.bsmAtt.toString()))
                    indy.plusAssign("name_and_type" to ("#" + constant.nameAndType.toString()))
                    indy
                }
                else -> hashMapOf<String, Any>("name" to "Skipped")
            })

        })

        return map
    }

    private fun transformMembers(members: Array<Member>): MutableList<LinkedHashMap<String, Any>> {
        val result: MutableList<LinkedHashMap<String, Any>> = mutableListOf()
        members.forEach { member ->
            val map: LinkedHashMap<String, Any> = linkedMapOf()
            map["access"]     = member.access()
            map["name"]       = member.name()
            map["desc"]       = member.desc()
            map["attributes"] = transformAttributes(member.attributes)

            result.add(map)
        }
        return result
    }

    private fun transformAttributes(attributes: Array<Attribute>): MutableList<LinkedHashMap<String, Any>> {
        val result: MutableList<LinkedHashMap<String, Any>> = mutableListOf()
        attributes.forEach { attribute ->
            val map: LinkedHashMap<String, Any> = linkedMapOf()
            map["name"] = attribute.javaClass.name
            when(attribute) {
                is BootstrapMethods -> {
                    map["bsm"] = attribute.bsm.map({
                        val bsm = linkedMapOf<String, Any>()
                        bsm.plusAssign("bsmRef" to it.first.replace("/", "."))
                        bsm.plusAssign("args"   to it.second.toMutableList())
                        bsm
                    }).toMutableList()
                }
                is Code -> {
                    map.plusAssign("maxStack"       to attribute.maxStack)
                    map.plusAssign("maxLocals"      to attribute.maxLocals)
                    map.plusAssign("exceptionTable" to attribute.exTable.map({
                        val ex = linkedMapOf<String, Any>()
                        ex.plusAssign("catchType" to it.second)
                        ex.plusAssign("startPc"   to it.first.first)
                        ex.plusAssign("endPc"     to it.first.second)
                        ex.plusAssign("handlePc"  to it.first.third)
                        ex
                    }).toMutableList())
                    map.plusAssign("opcodes"    to transformOpcodes(attribute.opcodes))
                    map.plusAssign("attributes" to transformAttributes(attribute.attributes))
                }
                is Exceptions -> {
                    map["exceptions"] = attribute.exceptions.toList()
                }
                is InnerClasses -> {
                    map["classes"] = attribute.classes.map({
                        val _class = linkedMapOf<String, String>()
                        _class.plusAssign("in"     to it[0])
                        _class.plusAssign("out"    to it[1])
                        _class.plusAssign("name"   to it[2])
                        _class.plusAssign("access" to it[3])
                        _class
                    }).toMutableList()
                }
                is LineNumberTable -> {
                    map.plusAssign("table" to attribute.table.map({
                        val table = linkedMapOf<String, Any>()
                        table.plusAssign("startPc"    to it[0])
                        table.plusAssign("endPc"      to it[1])
                        table.plusAssign("lineNumber" to it[2])
                        table
                    }).toMutableList())
                }
                is LocalVariable -> {
                    map.plusAssign("table" to attribute.table.map({
                        val table = linkedMapOf<String, Any>()
                        table.plusAssign("name"  to it.second.first)
                        table.plusAssign("desc"  to it.second.second)
                        table.plusAssign("start" to it.first.first)
                        table.plusAssign("end"   to it.first.second)
                        table.plusAssign("index" to it.first.third)
                        table
                    }).toMutableList())
                }
                is SourceFile -> {
                    map["source"] = attribute.source
                }
                is StackMapTable -> {
                    map["entries"] = attribute.entries
                }
            }

            result.add(map)
        }

        return result
    }

    private fun transformOpcodes(opcodes: Array<Opcode>): LinkedHashMap<Int, Any> {
        val result: LinkedHashMap<Int, Any> = linkedMapOf()
        opcodes.forEach({ opcode ->
            result.plusAssign(opcode.pc to when(opcode) {
                is Branch -> {
                    val branch = hashMapOf<String, Any>("name" to opcode.type)
                    branch.plusAssign("branch" to (opcode.branch + opcode.pc))
                    branch
                }
                is Iinc -> {
                    val iinc = hashMapOf<String, Any>("name" to opcode.type)
                    iinc.plusAssign("index" to opcode.index)
                    iinc.plusAssign("cons"  to opcode.cons)
                    iinc
                }
                is Index -> {
                    val index = hashMapOf<String, Any>("name" to opcode.type)
                    index.plusAssign("index" to opcode.value)
                    index
                }
                is Push -> {
                    val push = hashMapOf<String, Any>("name" to opcode.type)
                    push.plusAssign("value" to opcode.value)
                    push
                }
                is NewArray -> {
                    val na = hashMapOf<String, Any>("name" to opcode.type)
                    na.plusAssign("atype" to opcode.atype)
                    na
                }
                is TableSwitch -> {
                    val tableSwitch = hashMapOf<String, Any>("name" to opcode.type)
                    tableSwitch.plusAssign("default" to (opcode.default + opcode.pc))
                    tableSwitch.plusAssign("offsets" to opcode.offsets.map({ it + opcode.pc }).toMutableList())
                    tableSwitch
                }
                is LookupSwitch -> {
                    val lookup = hashMapOf<String, Any>("name" to opcode.type)
                    lookup.plusAssign("default"      to (opcode.default + opcode.pc))
                    lookup.plusAssign("matchOffsets" to opcode.matchOffset.map({
                        val matchOffset = hashMapOf<String, Int>()
                        matchOffset.plusAssign("match"  to it.first)
                        matchOffset.plusAssign("offset" to (it.second + opcode.pc))
                        matchOffset
                    }).toMutableList())
                    lookup
                }
                is InvokeInterface -> {
                    val invoke = hashMapOf<String, Any>("name" to opcode.type)
                    invoke.plusAssign("count"   to opcode.count)
                    invoke.plusAssign("index"   to opcode.index)
                    invoke.plusAssign("operand" to opcode.operand.replace("/", "."))
                    invoke
                }
                is MultiANewArray -> {
                    val mana = hashMapOf<String, Any>("name" to opcode.type)
                    mana.plusAssign("dimensions" to opcode.dimensions)
                    mana.plusAssign("index"      to opcode.index)
                    mana.plusAssign("operand"    to opcode.operand)
                    mana
                }
                is Wide -> {
                    val wide = hashMapOf<String, Any>("name" to opcode.type)
                    wide.plusAssign("tag"   to opcode.tag)
                    wide.plusAssign("const" to opcode._const)

                    val refToPool: ReferenceToPool = opcode.ref
                    val ref = hashMapOf<String, Any>("name" to refToPool.type)
                    ref.plusAssign("ldcType" to refToPool.ldcType)
                    ref.plusAssign("index"   to refToPool.index)
                    ref.plusAssign("operand" to refToPool.operand)
                    wide.plusAssign("ref" to ref)

                    wide
                }
                is ReferenceToPool -> {
                    val ref = hashMapOf<String, Any>("name" to opcode.type)
                    ref.plusAssign("ldcType" to opcode.ldcType)
                    ref.plusAssign("index"   to opcode.index)
                    ref.plusAssign("operand" to when(opcode.type) {
                        "invokevirtual",
                        "invokespecial",
                        "invokestatic",
                        "invokedynamic",
                        "getfield",
                        "putfield",
                        "getstatic",
                        "putstatic",
                        "_new",
                        "checkcast" -> opcode.operand.replace("/", ".")
                        else        -> opcode.operand
                    })
                    ref
                }
                else -> hashMapOf("name" to opcode.type)
            })
        })

        return result
    }
}