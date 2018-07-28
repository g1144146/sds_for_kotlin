package sds.classfile

import sds.classfile.constant_pool.Constant as Cons
import sds.classfile.constant_pool.ConstantValueExtractor as Extractor

interface ClassfileInformationInterface {
    val properties: HashMap<String, Any>
    fun extract(index: Int, pool: Array<Cons>): String = Extractor.extract(pool[index - 1], pool)
}

abstract class ClassfileInformation: ClassfileInformationInterface {
    override val properties: HashMap<String, Any> = hashMapOf()
}