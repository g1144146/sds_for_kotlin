package sds

import java.io.InputStream
import sds.classfile.Member
import sds.classfile.ClassfileStream as Stream
import sds.classfile.ClassfileStream.ImplForDataInputStream
import sds.classfile.ClassfileStream.ImplForRandomAccessFile
import sds.classfile.attribute.Attribute
import sds.classfile.constant_pool.Constant
import sds.classfile.constant_pool.ConstantAdapter as Adapter
import sds.classfile.constant_pool.ConstantValueExtractor.extract
import sds.classfile.constant_pool.Utf8Info as Utf8
import sds.classfile.constant_pool.Type.DOUBLE
import sds.classfile.constant_pool.Type.LONG
import sds.util.AccessFlag.get
import sds.util.DescriptorParser.removeLangPrefix

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

        val pool: Array<Constant> = classfile.pool
        pool.forEach { println(it) }
        classfile.access = get(data.short(), "class")
        classfile._this  = extract(data.short(), pool)
        classfile._super = removeLangPrefix(extract(data.short(), pool).replace("/", "."))
        classfile.interfaces = (0 until data.short()).map({ extract(data.short(), pool) }).toTypedArray()
        println("${classfile.access}${classfile._this} extends ${classfile._super}")

        val genAttribute: (Stream, Array<Constant>) -> Attribute = { _data, _pool ->
            val utf8: Utf8 = pool[_data.short() - 1] as Utf8
            Attribute.create(utf8.value, _data, _pool)
        }
        val genMember: (Int) -> Member = { Member(data, pool, genAttribute) }
        classfile.fields  = (0 until data.short()).map(genMember).toTypedArray()

        classfile.fields.forEach { println(it) }
//        classfile.methods = (0 until data.short()).map(genMember).toTypedArray()
//        classfile.attributes = (0 until data.short()).map({ genAttribute(data, pool) }).toTypedArray()
        data.close()
    }

    tailrec private fun readPool(len: Int, data: Stream, pool: MutableList<Constant>): Array<Constant> {
        if(pool.size == len) {
            return pool.toTypedArray()
        }
        val value: Constant = Constant.create(data)
        pool.add(value)
        return when(value.tag) {
            DOUBLE, LONG -> {
                pool.add(Adapter(-1))
                readPool(len, data, pool)
            }
            else -> readPool(len, data, pool)
        }
    }
}