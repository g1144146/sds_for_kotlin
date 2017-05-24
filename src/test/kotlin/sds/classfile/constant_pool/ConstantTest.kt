package sds.classfile.constant_pool

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldThrowTheException
import sds.classfile.constant_pool.ConstantValueExtractor.extract

class ConstantTest: Spek({
    val constants: Array<Constant> = arrayOf(
        Utf8Info("utf8"),
        NumberInfo(1),
        NumberInfo(2.0f),
        NumberInfo(3L),
        NumberInfo(4.0),
        ClassInfo(11),
        StringInfo(1),
        NameAndTypeInfo(1, 11),
        HandleInfo(1, 1),
        TypeInfo(11),
        InvokeDynamicInfo(1, 1),
        Utf8Info("Lorg/jetbrains/spek/api/Spek;"),
        MemberInfo(Type.FIELD, 1, 1),
        MemberInfo(Type.METHOD, 1, 1),
        MemberInfo(Type.INTERFACE, 1, 1)
    )

    describe("classes extend Constant") {
        on("Utf8Info") {
            it("should return Utf8\tutf8") { constants[0].toString().shouldEqualTo("Utf8\tutf8") }
        }
        on("NumberInfo") {
            it("should return Integer\t1")  { constants[1].toString().shouldEqualTo("Integer\t1")  }
            it("should return Float\t2.0")  { constants[2].toString().shouldEqualTo("Float\t2.0")  }
            it("should return Long\t3")     { constants[3].toString().shouldEqualTo("Long\t3")     }
            it("should return Double\t4.0") { constants[4].toString().shouldEqualTo("Double\t4.0") }
        }
        on("ClassInfo") {
            it("should return Class\t#1") { constants[5].toString().shouldEqualTo("Class\t#11") }
        }
        on("StringInfo") {
            it("should return String\t#1") { constants[6].toString().shouldEqualTo("String\t#1") }
        }
        on("NameAndTypeInfo") {
            it("should return NameAndType\t#1:#1") {
                constants[7].toString().shouldEqualTo("NameAndType\t#1:#11")
            }
        }
        on("HandleInfo from constant-pool or other reference index") {
            it("should return MethodHandle\tREF_getField:#1") {
                constants[8].toString().shouldEqualTo("MethodHandle\tREF_getField:#1")
            }
            it("should return MethodHandle\tREF_getStatic:#1") {
                HandleInfo(2, 1).toString().shouldEqualTo("MethodHandle\tREF_getStatic:#1")
            }
            it("should return MethodHandle\tREF_putField:#1") {
                HandleInfo(3, 1).toString().shouldEqualTo("MethodHandle\tREF_putField:#1")
            }
            it("should return MethodHandle\tREF_putStatic:#1") {
                HandleInfo(4, 1).toString().shouldEqualTo("MethodHandle\tREF_putStatic:#1")
            }
            it("should return MethodHandle\tREF_invokeVirtual:#1") {
                HandleInfo(5, 1).toString().shouldEqualTo("MethodHandle\tREF_invokeVirtual:#1")
            }
            it("should return MethodHandle\tREF_invokeStatic:#1") {
                HandleInfo(6, 1).toString().shouldEqualTo("MethodHandle\tREF_invokeStatic:#1")
            }
            it("should return MethodHandle\tREF_invokeSpecial:#1") {
                HandleInfo(7, 1).toString().shouldEqualTo("MethodHandle\tREF_invokeSpecial:#1")
            }
            it("should return MethodHandle\tREF_newInvokeSpecial:#1") {
                HandleInfo(8, 1).toString().shouldEqualTo("MethodHandle\tREF_newInvokeSpecial:#1")
            }
            it("should return MethodHandle\tREF_invokeInterface:#1") {
                HandleInfo(9, 1).toString().shouldEqualTo("MethodHandle\tREF_invokeInterface:#1")
            }
            it("should throw IllegalStateException") {
                { HandleInfo(10, 1).toString() }.shouldThrowTheException(IllegalStateException::class)
            }
        }
        on("TypeInfo") {
            it("should return MethodType\t#1") { constants[9].toString().shouldEqualTo("MethodType\t#11") }
        }
        on("InvokeDynamicInfo") {
            it("should return InvokeDynamic\t#1:#1") { constants[10].toString().shouldEqualTo("InvokeDynamic\t#1:#1") }
        }
        on("MemberInfo") {
            it("should return Field_ref\t#1.#1")  { constants[12].toString().shouldEqualTo("Field_ref\t#1.#1")  }
            it("should return Method_ref\t#1.#1") { constants[13].toString().shouldEqualTo("Method_ref\t#1.#1") }
            it("should return InterfaceMethod_ref\t#1.#1") {
                constants[14].toString().shouldEqualTo("InterfaceMethod_ref\t#1.#1")
            }
        }
    }

    describe("ConstantValueExtractor") {
        on("extract") {
            on("Utf8Info") {
                it("should be utf8") { extract(constants[0], constants).shouldBe("utf8") }
            }
            on("NumberInfo") {
                on("Integer") { it("should be 1")   { extract(constants[1], constants).shouldBe(1)    } }
                on("Float")   { it("should be 2.0") { extract(constants[2], constants).shouldBe(2.0f) } }
                on("Long")    { it("should be 3")   { extract(constants[3], constants).shouldBe(3L)   } }
                on("Double")  { it("should be 4.0") { extract(constants[4], constants).shouldBe(4.0)  } }
            }
            on("ClassInfo") {
                it("should be utf8") { extract((constants[5] as ClassInfo).index, constants).shouldBe("utf8") }
            }
            on("StringInfo") {
                it("should be utf8") { extract(constants[6], constants).shouldBe("utf8") }
            }
            on("NameAndType") {
                it("should be utf8:org.jetbrains.spek.api.Spek") {
                    extract(constants[7], constants).shouldBe("utf8:org.jetbrains.spek.api.Spek")
                }
            }
            on("HandleInfo") {
                it("should be utf8") { extract(constants[8], constants).shouldBe("utf8") }
            }
            on("TypeInfo") {
                it("should be org.jetbrains.spek.api.Spek") {
                    extract(constants[9], constants).shouldBe("org.jetbrains.spek.api.Spek")
                }
            }
            on("InvokeDynamicInfo") {
                it("should be utf8") { extract(constants[10], constants).shouldBe("utf8") }
            }
            on("MemberInfo") {
                it("should be utf8.utf8") { extract(constants[12], constants).shouldBe("utf8.utf8") }
                it("should be utf8.utf8") { extract(constants[13], constants).shouldBe("utf8.utf8") }
                it("should be utf8.utf8") { extract(constants[14], constants).shouldBe("utf8.utf8") }
            }
        }
    }
})