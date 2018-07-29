package sds

import java.io.InputStream
import sds.classfile.Member
import sds.classfile.ClassfileStream as Stream
import sds.classfile.attribute.Attribute
import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.constant_pool.ConstantAdapter as Adapter
import sds.classfile.constant_pool.ConstantValueExtractor.extract
import sds.classfile.constant_pool.Utf8Info as Utf8
import sds.classfile.constant_pool.Type
import sds.util.AccessFlag.get
import sds.util.DescriptorParser.removeLangPrefix

class ClassfileReader {
    val classfile: Classfile = Classfile()

    constructor(file: String) {
        read(Stream.create(file))
    }

    constructor(stream: InputStream) {
        read(Stream.create(stream))
    }

    private fun read(data: Stream) {
        classfile.magic  = Integer.toHexString(data.int())
        classfile.minor  = data.short()
        classfile.major  = data.short()
        classfile.pool   = readPool(data.short() - 1, data, mutableListOf())

        val pool: Array<Cons> = classfile.pool
//        pool.forEach { println(it) }
        classfile.access = get(data.short(), "class")
        classfile._this  = extract(data.short(), pool)
        classfile._super = removeLangPrefix(extract(data.short(), pool).replace("/", "."))
        classfile.interfaces = (0 until data.short()).map({ extract(data.short(), pool) }).toTypedArray()
//        println("${classfile.access}${classfile._this} extends ${classfile._super}")

        val genAttribute: (Stream, Array<Cons>) -> Attribute = { _data: Stream, _pool: Array<Cons> ->
            Attribute.create((_pool[_data.short() - 1] as Utf8).value, _data, _pool)
        }
        val genMember: (Int) -> Member = { Member(data, pool, genAttribute) }
        classfile.fields  = (0 until data.short()).map(genMember).toTypedArray()

//        classfile.fields.forEach { println(it) }
        classfile.methods = (0 until data.short()).map(genMember).toTypedArray()
        classfile.attributes = (0 until data.short()).map({ genAttribute(data, pool) }).toTypedArray()
        data.close()
    }

    tailrec private fun readPool(len: Int, data: Stream, pool: MutableList<Cons>): Array<Cons> {
        if(pool.size == len) {
            return pool.toTypedArray()
        }
        val tag: Int = data.byte()
        pool.add(Cons.create(tag, data))
        return when(tag) {
            Type.LONG, Type.DOUBLE -> {
                pool.add(Adapter())
                readPool(len, data, pool)
            }
            else -> readPool(len, data, pool)
        }
    }
}