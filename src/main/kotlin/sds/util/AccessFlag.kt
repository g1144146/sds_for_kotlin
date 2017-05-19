package sds.util

object AccessFlag {
    private val PUBLIC:       Pair<Int, String> = 0x0001 to "public "
    private val PRIVATE:      Pair<Int, String> = 0x0002 to "private "
    private val PROTECTED:    Pair<Int, String> = 0x0004 to "protected"
    private val STATIC:       Pair<Int, String> = 0x0008 to "static "
    private val FINAL:        Pair<Int, String> = 0x0010 to "final "
    private val SYNCHRONIZED: Pair<Int, String> = 0x0020 to "synchronized "
    private val VOLATILE:     Pair<Int, String> = 0x0040 to "volatile"
    private val TRANSIENT:    Pair<Int, String> = 0x0080 to "transient "
    private val NATIVE:       Pair<Int, String> = 0x0100 to "native "
    private val INTERFACE:    Pair<Int, String> = 0x0200 to "interface "
    private val ABSTRACT:     Pair<Int, String> = 0x0400 to "abstract "
    private val STRICT:       Pair<Int, String> = 0x0800 to "strictfp "
    private val SYNTHETIC:    Pair<Int, String> = 0x1000 to "synthetic"
    private val ANNOTATION:   Pair<Int, String> = 0x2000 to "@interface "
    private val ENUM:         Pair<Int, String> = 0x4000 to "enum "
    private val SUPER:        Int = 0x0020
    private val BRIDGE:       Int = 0x0040
    private val VARARGS:      Int = 0x0080
    private val MANDATED:     Int = 0x8000
    private val CLASS:  Int = PUBLIC.first   or FINAL.first     or SUPER            or INTERFACE.first or
                              ABSTRACT.first or SYNTHETIC.first or ANNOTATION.first or ENUM.first
    private val FIELD:  Int = PUBLIC.first   or PRIVATE.first   or PROTECTED.first or STATIC.first or FINAL.first or
                              VOLATILE.first or TRANSIENT.first or SYNTHETIC.first or ENUM.first
    private val METHOD: Int = PUBLIC.first or PRIVATE.first      or PROTECTED.first or STATIC.first or
                              FINAL.first  or SYNCHRONIZED.first or BRIDGE          or VARARGS      or
                              NATIVE.first or ABSTRACT.first     or STRICT.first    or SYNTHETIC.first
    private val NESTED: Int = PUBLIC.first    or PRIVATE.first  or PROTECTED.first or STATIC.first     or FINAL.first or
                              INTERFACE.first or ABSTRACT.first or SYNTHETIC.first or ANNOTATION.first or ENUM.first
    private val LOCAL:  Int = FINAL.first

    fun get(flag: Int, type: String): String = when {
        type == "class"  && checkOr(flag, CLASS)  -> getClassFlag(flag)
        type == "field"  && checkOr(flag, FIELD)  -> getFieldFlag(flag)
        type == "method" && checkOr(flag, METHOD) -> getMethodFlag(flag)
        type == "nested" && checkOr(flag, NESTED) -> getClassFlag(flag)
        type == "local"  && checkOr(flag, LOCAL)  -> getLocalFlag(flag)
        else -> throw IllegalArgumentException()
    }

    private fun getClassFlag(flag: Int): String  =
        build(flag, PUBLIC, STATIC, FINAL, ABSTRACT, SYNTHETIC, ANNOTATION, ENUM) + when {
            checkAnd(flag, INTERFACE.first) && ((flag and ANNOTATION.first) == 0) -> "interface "
            flag and (INTERFACE.first or ENUM.first or ANNOTATION.first)    == 0  -> "class "
            else -> ""
        }
    private fun getFieldFlag(flag: Int): String  =
        build(flag, PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, VOLATILE, TRANSIENT, SYNTHETIC, ENUM)
    private fun getMethodFlag(flag: Int): String =
        build(flag, PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, SYNCHRONIZED, NATIVE, ABSTRACT, STRICT, SYNTHETIC)
    private fun getLocalFlag(flag: Int): String  = if(checkAnd(flag, FINAL.first)) "final " else ""

    private fun build(target: Int, vararg flags: Pair<Int, String>): String =
        flags.filter { checkAnd(target, it.first)    } .map { it.second }
             .reduce { flag1, flag2 -> flag1 + flag2 }
    private fun checkAnd(target: Int, flag: Int): Boolean = (target and flag) == flag
    private fun checkOr(target:  Int, flag: Int): Boolean = (target or  flag) == flag
}