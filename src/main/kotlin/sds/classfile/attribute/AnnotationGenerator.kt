package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.attribute.Annotation as Ann
import sds.classfile.attribute.ArrayValue as AV
import sds.classfile.attribute.ElementValue as Value
import sds.classfile.attribute.ElementValuePair as Pair
import sds.classfile.attribute.EnumConstValue as Enum
import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.constant_pool.ConstantValueExtractor.extract
import sds.util.DescriptorParser.parse

object AnnotationGenerator {
    fun generate(data: ClassfileStream, pool: Array<Cons>): String = generate(Ann(data), pool)

    fun generate(ann: Ann, pool: Array<Cons>): String {
        val value: String = ann.pairs.map {
            "${extract(it.name, pool)} = ${generateFromElement(it.value, pool)}" }.joinToString(",")
        return "@${parse(extract(ann.type, pool))}($value)"
    }

    fun generateFromElement(element: Value, pool: Array<Cons>): String = when(element.tag) {
        'B', 'D', 'F', 'I', 'J', 'S', 'Z' -> extract((element.value as Int), pool)
        'C'  -> "'${extract(element.value as Int, pool)}'"
        's'  -> "\"${extract(element.value as Int, pool)}\""
        'c'  -> "${parse(extract(element.value as Int, pool))}.class"
        'e'  -> {
            val enum: Enum = element.value as Enum
            "${parse(extract(enum.typeName, pool))}.${extract(enum.constName, pool)}"
        }
        '@'  -> generate(element.value as Ann, pool)
        else -> "{${(element.value as AV).values.map { generateFromElement(it, pool) } .joinToString(",")}}"
    }
}