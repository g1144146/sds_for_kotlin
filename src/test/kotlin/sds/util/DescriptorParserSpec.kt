package sds.util

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.it
import org.amshove.kluent.shouldEqualTo
import sds.util.DescriptorParser.parse
import sds.util.DescriptorParser.removeLangPrefix

class DescriptorParserSpec: Spek({
    describe("descriptor parser") {
        on("removeLangPrefix") {
            val lang: String = "java.lang."
            val testCases: Array<String> = arrayOf(
                "${lang}Object", "${lang}String", "${lang}System.out", "${lang}annotation.Annotation",
                "${lang}instrument.Instrumentation", "${lang}invoke.MutableCallSite", "${lang}reflect.Field",
                "${lang}management.ManagementFactory", "${lang}ref.PhantomReference", "sds.util.AccessFlag"
            )
            it("should return Object.") { removeLangPrefix(testCases[0]).shouldEqualTo("Object") }
            it("should return String.") { removeLangPrefix(testCases[1]).shouldEqualTo("String") }
            it("should return System.out.") { removeLangPrefix(testCases[2]).shouldEqualTo("System.out") }
            it("should return java.annotation.Annotation.") {
                removeLangPrefix(testCases[3]).shouldEqualTo("java.lang.annotation.Annotation")
            }
            it("should return java.lang.instrument.Instrumentation.") {
                removeLangPrefix(testCases[4]).shouldEqualTo("java.lang.instrument.Instrumentation")
            }
            it("should return java.lang.invoke.MutableCallSite.") {
                removeLangPrefix(testCases[5]).shouldEqualTo("java.lang.invoke.MutableCallSite")
            }
            it("should return java.lang.reflect.Field.") {
                removeLangPrefix(testCases[6]).shouldEqualTo("java.lang.reflect.Field")
            }
            it("should return java.lang.management.ManagementFactory.") {
                removeLangPrefix(testCases[7]).shouldEqualTo("java.lang.management.ManagementFactory")
            }
            it("should return java.lang.ref.PhantomReference.") {
                removeLangPrefix(testCases[8]).shouldEqualTo("java.lang.ref.PhantomReference")
            }
            it("should return sds.util.AccessFlag.") {
                removeLangPrefix(testCases[9]).shouldEqualTo("sds.util.AccessFlag")
            }
        }

        on("parse") {
            on("field") {
                it("should return byte.")      { parse("B").shouldEqualTo("byte")         }
                it("should return char.")      { parse("C").shouldEqualTo("char")         }
                it("should return double.")    { parse("D").shouldEqualTo("double")       }
                it("should return float.")     { parse("F").shouldEqualTo("float")        }
                it("should return int.")       { parse("I").shouldEqualTo("int")          }
                it("should return long.")      { parse("J").shouldEqualTo("long")         }
                it("should return short.")     { parse("S").shouldEqualTo("short")        }
                it("should return boolean.")   { parse("Z").shouldEqualTo("boolean")      }
                it("should return void.")      { parse("V").shouldEqualTo("void")         }
                it("should return int[][][].") { parse("[[[I").shouldEqualTo("int[][][]") }
                it("should return String.")    { parse("Ljava/lang/String;").shouldEqualTo("String")       }
                it("should return Object[]")   { parse("[Ljava/lang/Object;").shouldEqualTo("Object[]")    }
                it("should return Class< ? >") { parse("Ljava/lang/Class<*>;").shouldEqualTo("Class< ? >") }
                it("should return org.jetbrains.spek.api.Spek") {
                    parse("Lorg/jetbrains/spek/api/Spek;").shouldEqualTo("org.jetbrains.spek.api.Spek")
                }
                it("should return org.jetbrains.spek.api.Spek[][]") {
                    parse("[[Lorg/jetbrains/spek/spi/Spek;").shouldEqualTo("org.jetbrains.spek.api.Spek[][]")
                }
                it("should return java.util.List") {
                    parse("Ljava/util/List<TK;>;").shouldEqualTo("java.util.List<K>")
                }
            }
            on("method") {
                it("should return (int,int,int)byte") { parse("(IIIRKKRR)B").shouldEqualTo("(int,int,int)byte") }
                it("should return (int,int,java.io.File,int)java.net.URL") {
                    parse("(RQRWQEIIPOHHLjava/io/File;GGI)Ljava/net/URL;")
                         .shouldEqualTo("(int,int,java.io.File,int)java.net.URL")
                }
            }
            on("signature") {
                val case1: String = "<K:Ljava/lang/Object;>Ljava/lang/Object;Ljava/lang/Runnable;"
                it("should return <K extends Object>Object,Runnable") {
                    parse(case1, true).shouldEqualTo("<K extends Object>Object,Runnable")
                }
                val case2: String = "<U:Ljava/lang/Object;T::Ljava/lang/Runnable;A:Ljava/lang/String;" +
                                    "R::Ljava/lang/Runnable;:Ljava/lang/Appendable;S:Ljava/lang/Thread;>()V"
                it("should return " +
                   "<U extends Object,T extends Runnable,A extends String," +
                   "R extends Runnable & Appendable,S extends Thread>()void") {
                    parse(case2, true).shouldEqualTo(
                        "<U extends Object,T extends Runnable,A extends String," +
                        "R extends Runnable & Appendable,S extends Thread>()void"
                    )
                }
                val case3: String = "<+Ljava/lang/Object;>Ljava/util/List;"
                it("should return <? extends Object>java.util.List") {
                    parse(case3).shouldEqualTo("<? extends Object>java.util.List")
                }
            }
        }
    }
})