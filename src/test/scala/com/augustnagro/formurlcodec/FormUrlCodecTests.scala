package com.augustnagro.formurlcodec

import com.augustnagro.formurlcodec.FormUrlCodec
import munit.FunSuite

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

class FormUrlCodecTests extends FunSuite:

  test("decodeRaw"):
    val encoded = "username=dan&age=100&favoriteColor=BLUE"
    val rawDecoded = FormUrlCodec.decodeRaw(encoded)
    assertEquals(
      rawDecoded,
      Vector("username" -> "dan", "age" -> "100", "favoriteColor" -> "BLUE")
    )

  test("decodeRaw no-value key"):
    val encoded = "email=test%40gmail.com&g-recaptcha-response="
    val rawDecoded = FormUrlCodec.decodeRaw(encoded)
    assertEquals(
      rawDecoded,
      Vector("email" -> "test@gmail.com", "g-recaptcha-response" -> "")
    )

  test("encodeRaw"):
    val encoded = "username=dan&age=100&favoriteColor=BLUE"
    val rawEncoded = FormUrlCodec.encodeRaw(
      "username" -> "dan",
      "age" -> "100",
      "favoriteColor" -> "BLUE"
    )
    assertEquals(rawEncoded, encoded)

  enum Status derives FormUrlCodec:
    case New, Admin

  case class User(
      a: String,
      b: Boolean,
      c: Byte,
      d: Int,
      e: Double,
      f: Long,
      g: Char,
      h: UUID,
      i: Status
  ) derives FormUrlCodec

  test("encode and decode User"):
    val u = User(
      a = "dan&mike",
      b = true,
      c = 1.toByte,
      d = -9473,
      e = 23.4583,
      f = 3947850L,
      g = 'x',
      h = UUID.fromString("cb34d6dc-5ded-47d9-a3a5-884b628a3673"),
      i = Status.Admin
    )
    val encodedUser =
      "a=dan%26mike&b=true&c=1&d=-9473&e=23.4583&f=3947850&g=x&h=cb34d6dc-5ded-47d9-a3a5-884b628a3673&i=Admin"
    assertEquals(u.formUrlEncode, encodedUser)
    assertEquals(summon[FormUrlCodec[User]].formUrlDecode(encodedUser), u)

  test("adt enum"):
    compileErrors(
      """
       enum Test derives FormUrlCodec:
         case A(i: Int)
         case B(c: String)
       """
    )

  case class OptField(a: Int, b: Option[Int], c: Int) derives FormUrlCodec

  test("optional fields"):
    val of = OptField(1, Some(2), 3)
    assertEquals(of.formUrlEncode, "a=1&b=2&c=3")
    val decoder = summon[FormUrlCodec[OptField]]
    assertEquals(decoder.formUrlDecode("a=1&b=&c=3"), OptField(1, None, 3))

end FormUrlCodecTests
