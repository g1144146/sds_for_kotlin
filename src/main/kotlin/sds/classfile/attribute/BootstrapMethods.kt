package sds.classfile.attribute

import sds.classfile.ClassfileStream
import sds.classfile.constant_pool.Constant

class BootstrapMethods(data: ClassfileStream, pool: Array<Constant>): Attribute {
    val bsm: Array<Pair<String, Array<String>>> = (0 until data.short()).map {
        val bsmRef: String = extract(data.short(), pool)
        val args: Array<String> = (0 until data.short()).map { extract(data.short(), pool) } .toTypedArray()
        bsmRef to args
    }.toTypedArray()
}