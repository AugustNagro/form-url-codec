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

  test("encodeRaw"):
    val encoded = "username=dan&age=100&favoriteColor=BLUE"
    val rawEncoded = FormUrlCodec.encodeRaw(
      "username" -> "dan",
      "age" -> "100",
      "favoriteColor" -> "BLUE"
    )
    assertEquals(rawEncoded, encoded)

  case class User(
      a: String,
      b: Boolean,
      c: Byte,
      d: Int,
      e: Float,
      f: Double,
      g: Long,
      h: Char,
      i: UUID
  ) derives FormUrlCodec

  test("encode and decode User"):
    val u = User(
      a = "dan&mike",
      b = true,
      c = 1.toByte,
      d = -9473,
      e = 2f,
      f = 23.4583,
      g = 3947850L,
      h = 'x',
      i = UUID.fromString("cb34d6dc-5ded-47d9-a3a5-884b628a3673")
    )
    val encodedUser =
      "a=dan%26mike&b=true&c=1&d=-9473&e=2&f=23.4583&g=3947850&h=x&i=cb34d6dc-5ded-47d9-a3a5-884b628a3673"
    assertEquals(u.formUrlEncode, encodedUser)
    assertEquals(summon[FormUrlCodec[User]].formUrlDecode(encodedUser), u)

end FormUrlCodecTests
