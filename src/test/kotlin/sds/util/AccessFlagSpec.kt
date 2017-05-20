package sds.util

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldThrowTheException
import sds.util.AccessFlag.get

class AccessFlagSpec: Spek({
    val PUBLIC:       Pair<Int, String> = 0x0001 to "public "
    val PRIVATE:      Pair<Int, String> = 0x0002 to "private "
    val PROTECTED:    Pair<Int, String> = 0x0004 to "protected"
    val STATIC:       Pair<Int, String> = 0x0008 to "static "
    val FINAL:        Pair<Int, String> = 0x0010 to "final "
    val SYNCHRONIZED: Pair<Int, String> = 0x0020 to "synchronized "
    val VOLATILE:     Pair<Int, String> = 0x0040 to "volatile"
    val TRANSIENT:    Pair<Int, String> = 0x0080 to "transient "
    val NATIVE:       Pair<Int, String> = 0x0100 to "native "
    val INTERFACE:    Pair<Int, String> = 0x0200 to "interface "
    val ABSTRACT:     Pair<Int, String> = 0x0400 to "abstract "
    val STRICT:       Pair<Int, String> = 0x0800 to "strictfp "
    val SYNTHETIC:    Pair<Int, String> = 0x1000 to "synthetic"
    val ANNOTATION:   Pair<Int, String> = 0x2000 to "@interface "
    val ENUM:         Pair<Int, String> = 0x4000 to "enum "

    fun build(vararg flags: Pair<Int, String>): Int =
        flags.map { it.first }.reduce { f_1: Int, f_2: Int -> f_1 or f_2 }

    describe("access flag") {
        on("get for class") {
            val flag1: Int = build(PUBLIC, FINAL, SYNTHETIC)
            it("should return \"public final synthetic class \"") {
                get(flag1, "class").shouldEqualTo("public final synthetic class ")
            }
            val flag2: Int = flag1 or ABSTRACT.first or ANNOTATION.first
            it("should return \"public final synthetic abstract @interface \"") {
                get(flag2, "class").shouldEqualTo("public final synthetic abstract @interface ")
            }
            val flag3: Int = flag1 or ENUM.first
            it("should return \"public final synthetic enum \"") {
                get(flag3, "class").shouldEqualTo("public final synthetic enum ")
            }
            val flag4: Int = flag1 or INTERFACE.first
            it("should return \"public final synthetic interface \"") {
                get(flag4, "class").shouldEqualTo("public final synthetic interface ")
            }
        }

        on("get for field") {
            val flag: Int =
                build(PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, VOLATILE, TRANSIENT, SYNTHETIC, ENUM)
            it("should return \"public private protected static final volatile transient synthetic enum \"") {
                get(flag, "field")
                    .shouldEqualTo("public private protected static final volatile transient synthetic enum ")
            }
        }

        on("get for method") {
            val flag: Int =
                build(PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, SYNCHRONIZED, NATIVE, ABSTRACT, STRICT, SYNTHETIC)
            it("""should return
                  public private protected static final synchronized native abstract strictfp synthetic """) {
                get(flag, "method")
                    .shouldEqualTo("public private protected static final " +
                                   "synchronized native abstract strictfp synthetic ")
            }
        }

        on("get for nested class") {
            val flag: Int = build(PUBLIC, STATIC, FINAL)
            it("should return \"public static final class \"") {
                get(flag, "nested").shouldEqualTo("public static final class ")
            }
        }

        on("get for local") {
            val flag: Int = FINAL.first
            it("should return \"final \"") {
                get(flag, "local").shouldEqualTo("final ")
            }
            it("should return 0-length string") {
                get(0, "local").shouldEqualTo("")
            }
        }

        on("get in case of wrong type or invalid flag") {
            val flag: Int = build(PUBLIC,    PRIVATE, PROTECTED, STATIC,   FINAL,  SYNCHRONIZED, VOLATILE,
                                  TRANSIENT, NATIVE,  INTERFACE, ABSTRACT, STRICT, SYNTHETIC,    ANNOTATION, ENUM)
            it("should throw IllegalArgumentException") {
                { get(flag, "local") }.shouldThrowTheException(IllegalArgumentException::class)
            }
            it("should throw IllegalArgumentException") {
                { get(PUBLIC.first, "") }.shouldThrowTheException(IllegalArgumentException::class)
            }
            it("should throw IllegalArgumentException") {
                { get(flag, "xxxx") }.shouldThrowTheException(IllegalArgumentException::class)
            }
        }
    }
})