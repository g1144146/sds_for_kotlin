package sds.classfile.attribute

import sds.classfile.ClassfileStream as Stream
import sds.classfile.constant_pool.Constant as Cons

class LocalVariable(name: String, data: Stream, pool: Array<Cons>): Attribute {
}