package sds.classfile.attribute

import sds.classfile.constant_pool.Constant

class Signature(sig: Int, pool: Array<Constant>): Attribute() {
    val signature: String = extract(sig, pool)
    override fun toString(): String = super.toString() + ": $signature"
}