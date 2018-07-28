package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream
import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.attribute.AnnotationGenerator.generate

class RuntimeAnnotations(val name: String, data: Stream, pool: Array<Cons>): Attribute() {
    val annotations: Array<String> = (0 until data.short()).map { generate(data, pool) } .toTypedArray()
    override fun toString(): String = "[$name]: ${annotations.joinToString(", ")}"
}