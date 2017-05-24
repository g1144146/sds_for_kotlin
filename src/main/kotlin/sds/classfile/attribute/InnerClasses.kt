package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant
import sds.util.AccessFlag.get

class InnerClasses(data: ClassfileStream, pool: Array<Constant>): Attribute {
    val classes: Array<Array<String>> = (0 until data.short()).map {x: Int ->
        val f: (Int) -> String = { if(check(x, pool.size)) extract(it, pool) else "" }
        val _in:  String = f(data.short())
        val out:  String = f(data.short())
        val name: String = f(data.short())
        val access: String = get(data.short(), "nested")
        arrayOf(_in, out, name, access)
    }.toTypedArray()
    private fun check(index: Int, size: Int): Boolean = (index - 1) in (0 until size)
}