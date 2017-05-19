package sds.classfile.constant_pool

class InvokeDynamicInfo(val bsmAtt: Int, val nameAndType: Int): Constant(Type.INVOKE_DYNAMIC) {
    override fun toString(): String = super.toString() + "\t#$bsmAtt:#$nameAndType"
}