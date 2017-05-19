package sds

import java.io.InputStream
import sds.classfile.ClassfileStream as Stream
import sds.classfile.ClassfileStream.ImplForDataInputStream
import sds.classfile.ClassfileStream.ImplForRandomAccessFile
import sds.classfile.constant_pool.Constant
import sds.classfile.constant_pool.ConstantAdapter as Adapter
import sds.classfile.constant_pool.Type.DOUBLE
import sds.classfile.constant_pool.Type.LONG
import sds.util.AccessFlag.get

class ClassfileReader {
    private val classfile: Classfile = Classfile()

    constructor(file: String) {
        val data: Stream = ImplForRandomAccessFile(file)
        read(data)
    }

    constructor(stream: InputStream) {
        val data: Stream = ImplForDataInputStream(stream)
        read(data)
    }

    private fun read(data: Stream) {
        classfile.magic  = data.int()
        classfile.minor  = data.short()
        classfile.major  = data.short()
        classfile.pool   = readPool(data.short() - 1, data, mutableListOf())
        classfile.access = get(data.short(), "class")
    }

    private fun readPool(len: Int, data: Stream, pool: MutableList<Constant>): Array<Constant> {
        if(pool.size == len) {
            return pool.toTypedArray()
        }
        pool.add(Constant.create(data))
        return when(pool.last().tag) {
            DOUBLE, LONG -> {
                pool.add(Adapter(-1))
                readPool(len, data, pool)
            }
            else -> readPool(len, data, pool)
        }
    }
}