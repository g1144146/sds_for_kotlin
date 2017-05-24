package sds.classfile

import java.io.RandomAccessFile
import java.io.DataInputStream
import java.io.InputStream

interface ClassfileStream: AutoCloseable {
    fun byte():   Int
    fun int():    Int
    fun float():  Float
    fun long():   Long
    fun double(): Double
    fun short():  Int
    fun unsignedByte(): Int
    fun skip(n: Int)
    fun fully(n: Int): ByteArray
    fun pointer(): Int

    companion object ClassfileStreamFactory {
        fun create(file: String):        ClassfileStream = ImplWithRandomAccessFile(file)
        fun create(stream: InputStream): ClassfileStream = ImplWithDataInputStream(stream)
    }

    private class ImplWithRandomAccessFile(file: String): ClassfileStream {
        private val raf: RandomAccessFile = RandomAccessFile(file, "r")

        override fun byte():   Int    = raf.readByte().toInt()
        override fun int():    Int    = raf.readInt()
        override fun float():  Float  = raf.readFloat()
        override fun long():   Long   = raf.readLong()
        override fun double(): Double = raf.readDouble()
        override fun short():  Int    = raf.readShort().toInt()
        override fun unsignedByte(): Int = raf.readUnsignedByte()
        override fun pointer(): Int = raf.filePointer.toInt()
        override fun skip(n: Int) {
            raf.skipBytes(n)
        }
        override fun fully(n: Int): ByteArray {
            val byte: ByteArray = ByteArray(n)
            raf.readFully(byte)
            return byte
        }
        override fun close() {
            raf.close()
        }
    }

    private class ImplWithDataInputStream(stream: InputStream): ClassfileStream {
        private val stream: DataInputStream = DataInputStream(stream)
        private var pointer: Int = 0

        override fun byte():   Int    = get(java.lang.Byte.BYTES,   stream.readByte().toInt())
        override fun int():    Int    = get(Integer.BYTES,          stream.readInt())
        override fun float():  Float  = get(java.lang.Float.BYTES,  stream.readFloat())
        override fun long():   Long   = get(java.lang.Long.BYTES,   stream.readLong())
        override fun double(): Double = get(java.lang.Double.BYTES, stream.readDouble())
        override fun short():  Int    = get(java.lang.Short.BYTES,  stream.readShort().toInt())
        override fun unsignedByte():  Int = get(java.lang.Byte.BYTES,  stream.readUnsignedByte())
        override fun pointer(): Int = pointer
        override fun skip(n: Int) {
            pointer += n
            stream.skipBytes(n)
        }
        override fun fully(n: Int): ByteArray {
            val byte: ByteArray = ByteArray(n)
            stream.readFully(byte)
            return get(n, byte)
        }
        override fun close() {
            stream.close()
        }
        private fun <T> get(n: Int, read: T): T {
            pointer += n
            return read
        }
    }
}
