package sds.classfile.constant_pool

class NameAndTypeInfo(val name: Int, val type: Int): Constant(Type.NAME_AND_TYPE) {
    override fun toString(): String = super.toString() + "\t#$name:#$type"
}