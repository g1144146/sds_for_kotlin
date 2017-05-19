package sds.classfile.attribute

import sds.classfile.constant_pool.Constant

class ConstantValue(constVal: Int, pool: Array<Constant>): Attribute() {
    val value: String = extract(constVal, pool)
    override fun toString(): String = super.toString() + ": $value"
}