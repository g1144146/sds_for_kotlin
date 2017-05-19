package sds.classfile.constant_pool

import sds.util.DescriptorParser.parse
import sds.util.DescriptorParser.removeLangPrefix

object ConstantValueExtractor {
    fun extract(index: Int, pool: Array<Constant>): String = extract(pool[index - 1], pool)
    fun extract(info: Constant, pool: Array<Constant>): String = when(info) {
        is Utf8Info          -> info.value
        is NumberInfo        -> info.number.toString()
        is StringInfo        -> extract(pool[info.string - 1], pool)
        is ClassInfo         -> removeLangPrefix(extract(pool[info.index - 1], pool))
        is HandleInfo        -> extract(pool[info.index - 1], pool)
        is TypeInfo          -> parse(extract(pool[info.desc - 1], pool))
        is InvokeDynamicInfo -> extract(pool[info.nameAndType - 1], pool)
        is MemberInfo        -> extract(pool[info._class - 1], pool) + "." + extract(pool[info.nameAndType - 1], pool)
        is NameAndTypeInfo   -> extract(pool[info.name - 1], pool)   + "|" + parse(extract(pool[info.type - 1], pool))
        else -> throw IllegalArgumentException("unknown constant info type.")
    }
}