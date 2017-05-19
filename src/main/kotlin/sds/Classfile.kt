package sds

import sds.classfile.ClassfileStream
import sds.classfile.Member
import sds.classfile.attribute.Attribute
import sds.classfile.constant_pool.Constant
import sds.classfile.constant_pool.ConstantAdapter

class Classfile {
    var magic:  Int = -1
    var major:  Int = -1
    var minor:  Int = -1
    var access: String = ""
    var _this:  String = ""
    var _super: String = ""
    var interfaces: Array<String>    = arrayOf()
    var pool:       Array<Constant>  = arrayOf()
    var fields:     Array<Member>    = arrayOf()
    var methods:    Array<Member>    = arrayOf()
    var attributes: Array<Attribute> = arrayOf()
}