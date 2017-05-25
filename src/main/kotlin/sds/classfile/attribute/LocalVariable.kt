package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream
import sds.classfile.constant_pool.Constant as Cons
import sds.util.DescriptorParser.parse

class LocalVariable(val name: String, data: Stream, pool: Array<Cons>): Attribute {
    val table: Array<Pair<Triple<Int, Int, Int>, Pair<String, String>>> = (0 until data.short()).map {
        val start: Int = data.short()
        val end:   Int = data.short() + start
        val _name: String = extract(data.short(), pool)
        val descIndex: Int = data.short()
        val index: Int = data.short()
        val desc: String = if(descIndex - 1 > 0) parse(extract(descIndex, pool)) else ""
        Triple(start, end, index) to (_name to desc)
    }.toTypedArray()
}