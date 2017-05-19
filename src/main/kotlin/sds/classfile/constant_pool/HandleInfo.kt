package sds.classfile.constant_pool

class HandleInfo(val kind: Int, val index: Int): Constant(Type.HANDLE) {
    override fun toString(): String = super.toString() + "\t" + when(kind) {
        1 -> "REF_get"
        2 -> "REF_get"
        3 -> "REF_put"
        4 -> "REF_put"
        5 -> "REF_invokeVirtual"
        6 -> "REF_invokeStatic"
        7 -> "REF_invokeSpecial"
        8 -> "REF_newInvokeSpecial"
        9 -> "REF_invokeInterface"
        else -> throw IllegalStateException("invalid reference kind value ($kind)")
    } + ":#" + index
}