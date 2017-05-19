package sds.classfile.constant_pool

class Utf8Info(val value: String): Constant(Type.UTF8) {
    override fun toString(): String = super.toString() + "\t$value"
}
