package sds.classfile.constant_pool

class MemberInfo(val type: Int, val _class: Int, val nameAndType: Int): Constant() {
    override fun toString(): String = when(type) {
        Type.FIELD  -> "Field_ref"
        Type.METHOD -> "Method_ref"
        else -> "InterfaceMethod_ref"
    } + "\t#$_class.#$nameAndType"
}