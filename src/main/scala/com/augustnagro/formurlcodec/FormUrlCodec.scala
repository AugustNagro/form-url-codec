package com.augustnagro.formurlcodec

import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.util.UUID
import scala.deriving.Mirror
import scala.compiletime.*
import scala.collection.mutable as m

trait FormUrlCodec[A]:
  extension (a: A) def formUrlEncode: String
  def formUrlDecode(s: String): A

object FormUrlCodec:

  private inline def decode(s: String): String =
    URLDecoder.decode(s, StandardCharsets.UTF_8)

  private inline def encode(s: String): String =
    URLEncoder.encode(s, StandardCharsets.UTF_8)

  def encodeRaw(kv: (Any, Any)*): String =
    kv.iterator
      .map((k, v) => encode(k.toString) + "=" + encode(v.toString))
      .mkString("&")

  def decodeRaw(s: String): Vector[(String, String)] =
    s
      .split('&')
      .map(kvString =>
        kvString.split('=') match
          case Array(k, v) => (decode(k), decode(v))
      )
      .toVector

  given StringCodec: FormUrlCodec[String] with
    extension (s: String) def formUrlEncode: String = encode(s)
    def formUrlDecode(s: String): String = decode(s)

  given BooleanCodec: FormUrlCodec[Boolean] with
    extension (b: Boolean) def formUrlEncode: String = b.toString
    def formUrlDecode(s: String): Boolean = decode(s).toBoolean

  given ByteCodec: FormUrlCodec[Byte] with
    extension (b: Byte) def formUrlEncode: String = b.toString
    def formUrlDecode(s: String): Byte = decode(s).toByte

  given ShortCodec: FormUrlCodec[Short] with
    extension (s: Short) def formUrlEncode: String = s.toString
    def formUrlDecode(s: String): Short = decode(s).toShort

  given IntCodec: FormUrlCodec[Int] with
    extension (i: Int) def formUrlEncode: String = i.toString
    def formUrlDecode(s: String): Int = decode(s).toInt

  given FloatCodec: FormUrlCodec[Float] with
    extension (f: Float) def formUrlEncode: String = f.toString
    def formUrlDecode(s: String): Float = decode(s).toFloat

  given DoubleCodec: FormUrlCodec[Double] with
    extension (d: Double) def formUrlEncode: String = d.toString
    def formUrlDecode(s: String): Double = decode(s).toDouble

  given LongCodec: FormUrlCodec[Long] with
    extension (l: Long) def formUrlEncode: String = l.toString
    def formUrlDecode(s: String): Long = decode(s).toLong

  given CharCodec: FormUrlCodec[Char] with
    extension (c: Char) def formUrlEncode: String = encode(c.toString)
    def formUrlDecode(s: String): Char = decode(s).head

  given UUIDCodec: FormUrlCodec[UUID] with
    extension (uuid: UUID) def formUrlEncode: String = uuid.toString
    def formUrlDecode(s: String): UUID = UUID.fromString(decode(s))

  inline given derived[A](using m: Mirror.ProductOf[A]): FormUrlCodec[A] =
    type Mets = m.MirroredElemTypes
    type Mels = m.MirroredElemLabels
    new FormUrlCodec[A]:
      extension (a: A)
        def formUrlEncode: String = encodeProduct[Mets, Mels](
          a.asInstanceOf[Product],
          Array.ofDim(constValue[Tuple.Size[Mets]])
        )
      def formUrlDecode(s: String): A =
        val kvArray = s
          .split('&')
          .map(kvString =>
            kvString.split('=') match
              case Array(k, v) => (decode(k), decode(v))
          )
        val resArray = Array.ofDim[Any](constValue[Tuple.Size[Mets]])
        decodeProduct[Mets, Mels, A](kvArray, resArray, m)

  private inline def encodeProduct[Mets, Mels](
      p: Product,
      andParts: Array[String],
      i: Int = 0
  ): String =
    inline (erasedValue[Mets], erasedValue[Mels]) match
      case _: (EmptyTuple, EmptyTuple) =>
        andParts.mkString("&")
      case _: (met *: metTail, mel *: melTail) =>
        val key = encode(constValue[mel].toString)
        val codec = summonInline[FormUrlCodec[met]]
        val value = codec.formUrlEncode(p.productElement(i).asInstanceOf[met])
        andParts(i) = key + "=" + value
        encodeProduct[metTail, melTail](p, andParts, i + 1)

  private inline def decodeProduct[Mets, Mels, A](
      kv: Array[(String, String)],
      resArr: Array[Any],
      m: Mirror.ProductOf[A],
      i: Int = 0
  ): A =
    inline (erasedValue[Mets], erasedValue[Mels]) match
      case _: (EmptyTuple, EmptyTuple) =>
        m.fromProduct(ArrayProduct(resArr))
      case _: (met *: metTail, mel *: melTail) =>
        val codec = summonInline[FormUrlCodec[met]]
        val key = decode(constValue[mel].toString)
        val (_, value) = kv.find((k, _) => k == key).get
        resArr(i) = codec.formUrlDecode(value)
        decodeProduct[metTail, melTail, A](kv, resArr, m, i + 1)

end FormUrlCodec
