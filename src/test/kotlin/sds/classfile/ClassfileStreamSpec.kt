package sds.classfile

import java.lang.ClassLoader
import java.io.InputStream as Input
import java.nio.file.Paths
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.it
import org.amshove.kluent.shouldBe
import sds.classfile.ClassfileStream as Stream

class ClassfileStreamSpec: Spek({
    describe("read from ClassfileStream") {
        val loader: ClassLoader = this.javaClass.classLoader
        on("ImplWithRandomAccessFile") {
            val data1: Stream = Stream.create(Paths.get(loader.getResource("Hello.class").toURI()).toString())
            data1.byte()         //  1
            data1.unsignedByte() //  1 +  1 = 2
            data1.int()          //  2 +  4 = 6
            data1.float()        //  6 +  4 = 10
            data1.long()         // 10 +  8 = 18
            data1.double()       // 18 +  8 = 26
            data1.short()        // 26 +  2 = 28
            data1.skip(10)       // 28 + 10 = 38
            val point: Int = data1.pointer()
            it("should be 38") { point.shouldBe(38) }
            val array: ByteArray = data1.fully(12)
            it("should be 12") { array.size.shouldBe(12) }
            data1.close()
        }
        on("ImplDataInputStream") {
            val input: Input = loader.getResourceAsStream("Hello.class")
            val data2: Stream = Stream.create(input)
            data2.byte()
            data2.unsignedByte()
            data2.int()
            data2.float()
            data2.long()
            data2.double()
            data2.short()
            data2.skip(10)
            val point: Int = data2.pointer()
            it("should be 38") { point.shouldBe(38) }
            val array: ByteArray = data2.fully(12)
            it("should be 12") { array.size.shouldBe(12) }
            data2.close()
        }
    }
})