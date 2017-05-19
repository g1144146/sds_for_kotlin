package sds.classfile.constant_pool

class TypeInfo(val desc: Int): Constant(Type.TYPE) {
    override fun toString(): String = super.toString() + "#$desc"
}