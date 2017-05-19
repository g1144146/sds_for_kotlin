package sds.classfile

import java.io.RandomAccessFile
import java.io.DataInputStream
import java.io.InputStream

sealed class ClassfileStream: AutoCloseable {
    abstract fun char():   Char
    abstract fun byte():   Int
    abstract fun int():    Int
    abstract fun float():  Float
    abstract fun long():   Long
    abstract fun double(): Double
    abstract fun short():  Int
    abstract fun unsignedByte():  Int
    abstract fun unsignedShort(): Int
    abstract fun skip(n: Int)
    abstract fun fully(n: Int): ByteArray
    abstract fun pointer(): Long

    class ImplForRandomAccessFile(file: String): ClassfileStream() {
        private val raf: RandomAccessFile = RandomAccessFile(file, "r")

        override fun char():   Char   = raf.readChar()
        override fun byte():   Int    = raf.readByte().toInt()
        override fun int():    Int    = raf.readInt()
        override fun float():  Float  = raf.readFloat()
        override fun long():   Long   = raf.readLong()
        override fun double(): Double = raf.readDouble()
        override fun short():  Int    = raf.readShort().toInt()
        override fun unsignedByte():  Int = raf.readUnsignedByte()
        override fun unsignedShort(): Int = raf.readUnsignedShort()
        override fun pointer(): Long = raf.filePointer
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

    class ImplForDataInputStream(stream: InputStream): ClassfileStream() {
        private val stream: DataInputStream = DataInputStream(stream)
        private var pointer: Long = 0

        override fun char():   Char   = get(Character.BYTES,        stream.readChar())
        override fun byte():   Int    = get(java.lang.Byte.BYTES,   stream.readByte().toInt())
        override fun int():    Int    = get(Integer.BYTES,          stream.readInt())
        override fun float():  Float  = get(java.lang.Float.BYTES,  stream.readFloat())
        override fun long():   Long   = get(java.lang.Long.BYTES,   stream.readLong())
        override fun double(): Double = get(java.lang.Double.BYTES, stream.readDouble())
        override fun short():  Int    = get(java.lang.Short.BYTES,  stream.readShort().toInt())
        override fun unsignedByte():  Int = get(java.lang.Byte.BYTES,  stream.readUnsignedByte())
        override fun unsignedShort(): Int = get(java.lang.Short.BYTES, stream.readUnsignedShort())
        override fun pointer(): Long = pointer
        override fun skip(n: Int) {
            stream.skipBytes(n)
        }
        override fun fully(n: Int): ByteArray {
            val byte: ByteArray = ByteArray(n)
            stream.readFully(byte)
            return byte
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
