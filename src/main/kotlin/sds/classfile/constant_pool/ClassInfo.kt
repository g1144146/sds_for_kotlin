package sds.classfile.constant_pool

class ClassInfo(val index: Int): Constant(Type.CLASS) {
    override fun toString(): String = super.toString() + "\t#$index"
}