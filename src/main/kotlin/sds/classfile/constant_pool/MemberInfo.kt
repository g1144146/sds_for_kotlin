package sds.classfile.constant_pool

class MemberInfo(type: Int, val _class: Int, val nameAndType: Int): Constant(type) {
    override fun toString(): String = super.toString() + "\t#$_class.#$nameAndType"
}