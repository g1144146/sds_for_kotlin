package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream
import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.attribute.AnnotationGenerator.generate

class RuntimeParameterAnnotations(val name: String, data: Stream, pool: Array<Cons>): Attribute() {
    val annotations: Array<Array<String>> = (0 until data.byte()).map {
        (0 until  data.short()).map {generate(data, pool) } .toTypedArray()
    }.toTypedArray()
    override fun toString(): String = "[$name]: [${annotations.flatten().joinToString(", ")}]"
}