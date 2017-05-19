package sds.classfile.constant_pool

class NumberInfo(private val number: Number, type: Int): Constant(type) {
    fun getValue(): Number = when(number) {
        is Int    -> number.toInt()
        is Float  -> number.toFloat()
        is Long   -> number.toLong()
        is Double -> number.toDouble()
        else -> throw IllegalStateException("unknown number type.")
    }

    override fun toString(): String = super.toString() + "\t" + getValue()
}