package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream

class RuntimeTypeAnnotations(val name: String, data: Stream): Attribute {
    val annotations: Array<TypeAnnotation> = (0 until data.short()).map {
        val target: Target = Target.create(data)
        val path: Array<Pair<Int, Int>> = (0 until data.unsignedByte()).map {
            data.byte() to data.byte() } .toTypedArray()
        TypeAnnotation(data, target, path)
    }.toTypedArray()
}

class TypeAnnotation(data: Stream, val target: Target, val path: Array<Pair<Int, Int>>): Annotation(data)

sealed class Target {
    override fun toString(): String = "${this.javaClass.simpleName}Target"
    companion object TargetFactory {
        fun create(data: Stream): Target = when(data.unsignedByte()) {
            0x00, 0x01      -> TypeParam(data.unsignedByte())
            0x10            -> SuperType(data.short())
            0x11, 0x12      -> TypeParamBound(data.unsignedByte(), data.unsignedByte())
            in 0x13 .. 0x15 -> Empty()
            0x16            -> MethodFormalParam(data.unsignedByte())
            0x17            -> Throws(data.short())
            0x40, 0x41      -> LocalVar(data)
            0x42            -> Catch(data.short())
            in 0x43 .. 0x46 -> Offset(data.short())
            in 0x47 .. 0x4B -> TypeArg(data.short(), data.unsignedByte())
            else -> throw RuntimeException("unknown target type.")
        }
    }

    class Empty: Target()
    class TypeParam(val type: Int): Target()
    class SuperType(val type: Int): Target()
    class MethodFormalParam(val formal: Int): Target()
    class Throws(val throws: Int): Target()
    class Catch(val exTable: Int): Target()
    class Offset(val offset: Int): Target()
    class TypeParamBound(val type: Int, val bounds: Int): Target()
    class TypeArg(val offset: Int, val typeArg: Int): Target()
    class LocalVar(data: Stream): Target() {
        val table: Array<Triple<Int, Int, Int>> = (0 until data.short()).map {
            Triple(data.short(), data.short(), data.short())
        }.toTypedArray()
    }
}