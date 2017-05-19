package sds.classfile.constant_pool

sealed class NumberInfo(private val number: Number, type: Int): Constant(type) {
    override fun toString(): String = super.toString() + "\t" + when(tag) {
        Type.INT    -> number.toInt()
        Type.FIELD  -> number.toFloat()
        Type.LONG   -> number.toLong()
        Type.DOUBLE -> number.toDouble()
        else -> throw IllegalStateException("unknown number type.")
    }
}

class IntInfo(val value: Int):       NumberInfo(value, Type.INT)
class FloatInfo(val value: Float):   NumberInfo(value, Type.FLOAT)
class LongInfo(val value: Long):     NumberInfo(value, Type.LONG)
class DoubleInfo(val value: Double): NumberInfo(value, Type.DOUBLE)