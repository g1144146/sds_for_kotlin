package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class EnclosingMethod(data: ClassfileStream, pool: Array<Constant>): Attribute() {
    val _class: String = extract(data.short(), pool)
    val method: String = { x: Int -> if(x - 1 > 0) extract(x, pool) else "" }(data.short())
    override fun toString(): String = "[EnclosingMethod]: ${_class}.$method"
}