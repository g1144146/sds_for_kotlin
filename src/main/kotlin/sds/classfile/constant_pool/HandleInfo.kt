package sds.classfile.constant_pool

class HandleInfo(val kind: Int, val index: Int): Constant() {
    override fun toString(): String = "MethodHandle\t" + when(kind) {
        1 -> "REF_getField"
        2 -> "REF_getStatic"
        3 -> "REF_putField"
        4 -> "REF_putStatic"
        5 -> "REF_invokeVirtual"
        6 -> "REF_invokeStatic"
        7 -> "REF_invokeSpecial"
        8 -> "REF_newInvokeSpecial"
        9 -> "REF_invokeInterface"
        else -> throw IllegalStateException("invalid reference kind value ($kind)")
    } + ":#$index"
}