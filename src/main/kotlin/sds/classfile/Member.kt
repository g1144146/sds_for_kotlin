package sds.classfile

import sds.classfile.ClassfileStream as Stream
import sds.classfile.attribute.Attribute
import sds.classfile.constant_pool.Constant as Cons
import sds.util.AccessFlag.get
import sds.util.DescriptorParser.parse

class Member(data: Stream, pool: Array<Cons>, f: (Stream, Array<Cons>) -> Attribute): ClassfileInformation() {
//    init {
//        // read header
//        val access: Int = data.short()
//        val nameIndex:   Int = data.short()
//        val descIndex: Int = data.short()
//
//        // to string
//        val name: String = extract(nameIndex, pool)
//        val desc: String = parse(extract(descIndex, pool))
//        val type: String = if("(" in desc) "method" else "field"
//
//        // gen map
//        super.properties["name"]      = name
//        super.properties["desc"]      = desc
//        super.properties["type"]      = type
//        super.properties["access"]    = get(access, type)
//        super.properties["attribute"] = (0 until data.short()).map({ f(data, pool) }).toTypedArray()
//    }
    private val declaration: Array<String> = ({ ->
        val access: Int = data.short()
        val nameIndex:   Int = data.short()
        val descIndex: Int = data.short()
        val name: String = extract(nameIndex, pool)
        val desc: String = parse(extract(descIndex, pool))
        arrayOf(get(access, if("(" in desc) "method" else "field"), name, desc)
    })()
    val attributes: Array<Attribute> = (0 until data.short()).map({ f(data, pool) }).toTypedArray()

    fun access(): String = declaration[0]
    fun name():   String = declaration[1]
    fun desc():   String = declaration[2]
    fun type():   String = if("(" in desc()) "method" else "field"
    override fun toString(): String = when(type()) {
        "field" -> access() + desc() + " " + name()
        else    -> access() + name() + desc()
    }
}