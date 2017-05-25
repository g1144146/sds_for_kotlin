package sds.classfile.attribute

import sds.classfile.ClassfileStream

open class Annotation(data: ClassfileStream) {
    val type: Int = data.short()
    val pairs: Array<ElementValuePair> = (0 until data.short()).map { ElementValuePair(data) } .toTypedArray()
}

class ArrayValue(data: ClassfileStream) {
    val values: Array<ElementValue> = (0 until data.short()).map { ElementValue(data) } .toTypedArray()
}

class ElementValuePair(data: ClassfileStream) {
    val name: Int = data.short()
    val value: ElementValue = ElementValue(data)
}

class ElementValue(data: ClassfileStream) {
    val tag: Char = data.byte() as Char
    val value: Any = when(tag) {
        in 'B'..'D', 'F', 'I', 'J', 'S', 'Z', 'c', 's' -> data.short()
        'e'  -> EnumConstValue(data)
        '@'  -> Annotation(data)
        '['  -> ArrayValue(data)
        else -> throw RuntimeException("unknown tag: $tag")
    }
}

class EnumConstValue(data: ClassfileStream) {
    val typeName:  Int = data.short()
    val constName: Int = data.short()
}