package sds

import sds.classfile.Member
import sds.classfile.attribute.Attribute
import sds.classfile.constant_pool.Constant
import sds.classfile.constant_pool.ConstantAdapter

class Classfile {
    var magic:  Int = -1
    var major:  Int = -1
    var minor:  Int = -1
    var access: String = ""
    var _this:  Int = -1
    var _super: Int = -1
    var interfaces: Array<Int> = arrayOf(0)
    var pool:   Array<Constant>  = arrayOf(ConstantAdapter(-1))
    var fields: Array<Member>    = arrayOf(Member())
    var methos: Array<Member>    = arrayOf(Member())
    var attributes: Array<Attribute> = arrayOf(Attribute())
}