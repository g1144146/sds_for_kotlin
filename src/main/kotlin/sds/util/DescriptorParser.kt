package sds.util

import kotlin.text.Regex
import kotlin.text.MatchResult

object DescriptorParser {
    private const val OBJ:  String = """L[a-z\.]*[0-9a-zA-Z_\$\.]+"""
    private const val LANG: String = "java.lang"
    private val langPackage: Array<String> = arrayOf(
        "$LANG.annotation", "$LANG.instrument", "$LANG.invoke",
        "$LANG.management", "$LANG.reflect",    "$LANG.ref"
    )

    fun parse(desc: String): String = parse(desc, false)
    fun parse(desc: String, isFieldSignature: Boolean): String {
        val obj:      String = """($OBJ|\[$OBJ)"""
        val prim:     String = """(B|C|D|F|I|J|S|Z|V|\[+B|\[+C|\[+D|\[+F|\[+I|\[+J|\[+S|\[+Z)"""
        val paren:    String = """(\(|\))"""
        val generics: String = """(T[A-Z]|\[+T[A-Z])"""
        val colon:    String = "(;:|::|:)"
        val wildCard: String = """(\+|\*)"""
        val diamond:  String = "(<|>)"
        val replaced: String = desc.replace("/", ".").replace(";>", ">").replace(";)", ")")
        val regex: Regex = """$obj|$prim|$paren|$generics|$colon|$wildCard|$diamond|([A-Z])|(;)""".toRegex()
        var beforeParen = true
        val parsed: String = regex.findAll(replaced).map({ matched: MatchResult ->
            val m: String = matched.value
            val len: Int = m.length
            println(">>> $m")
            when {
                m.startsWith("[") -> {
                    val last: Int = m.lastIndexOf("[") + 1
                    when {
                        m.matches(prim.toRegex()) -> {
                            val type: String = parsePrimitive(m.substring(len - 1))
                            type.substring(0, type.length - 1)
                        }
                        else -> {
                            val type: String = m.substring(last + 1, len)
                            removeLangPrefix(type)
                        }
                    } + (0 until last)
                            .map { "[]" }
                            .reduce { str_1: String, str_2: String -> str_1 + str_2 }
                }
                m.startsWith("L") or m.matches("T[A-Z]+".toRegex()) -> {
                    removeLangPrefix(m.substring(1, len))
                }
                m.matches("""\(|\)|<|>""".toRegex())                    -> {
                    beforeParen = (! ((m == "(") or (m == ")")))
                    m
                }
                m == ";:"                       -> " & "
                m.matches("::|:".toRegex())     -> " extends "
                m == "*"                        -> " ? "
                m == "+"                        -> "? extends "
                m == ";"                        -> ","
                m.matches("[A-Z]".toRegex()) and isFieldSignature && beforeParen -> m
                parsePrimitive(m).isNotEmpty()  -> parsePrimitive(m)
                else -> ""
            }
        }).reduce { matched_1: String, matched_2: String -> matched_1 + matched_2 }
        return arrayOf(parsed)
                .map { s: String -> if(s.endsWith(","))  s.substring(0, s.length - 1) else s }
                .map { s: String -> if(s.contains(",)")) s.replace(",)", ")")         else s }[0]
    }

    fun removeLangPrefix(desc: String): String =
        if(! desc.startsWith(LANG) or langPackage.filter({ desc.startsWith(it) }).isNotEmpty())
             desc
        else desc.replace("$LANG.", "")

    private fun parsePrimitive(desc: String): String = when(desc) {
        "B"  -> "byte,"
        "C"  -> "char,"
        "D"  -> "double,"
        "F"  -> "float,"
        "I"  -> "int,"
        "J"  -> "long,"
        "S"  -> "short,"
        "Z"  -> "boolean,"
        "V"  -> "void"
        else -> ""
    }
}