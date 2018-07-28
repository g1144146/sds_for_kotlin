package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class Signature(data: ClassfileStream, pool: Array<Constant>): Attribute() {
    val signature: String = extract(data.short(), pool)
    override fun toString(): String = "[Signature]: $signature"
}