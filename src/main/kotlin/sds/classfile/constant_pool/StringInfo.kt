package sds.classfile.constant_pool

class StringInfo(val string: Int): Constant(Type.STRING) {
    override fun toString(): String = super.toString() + "\t#$string"
}