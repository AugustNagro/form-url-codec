## form-url-codec

A simple Typeclass for deriving case-classes from `application/x-www-form-urlencoded` strings.

## Installing

`"com.augustnagro" %% "form-url-codec" % "0.1.0"`

## Usage

Simply add `derives FormUrlCodec` to a case class, like

```scala
case class User(userName: String, age: Int) derives FormUrlCodec
```

You can convert an instance of User to a `application/x-www-form-urlencoded` string via

```scala
val u = User("mike&dan", 23)
val encodedU = u.formUrlEncode // "userName=mike%26dan&age=23"
```

The reverse is possible with

```scala
val encodedUser = "userName=mike%26dan&age=23"
val u = summon[FormUrlCodec[User]].formUrlDecode(encodedUser) // User("mike&dan", 23)
```

The FormUrlCodec companion object also has methods for raw encoding / decoding:

```scala
val rawEncoded = FormUrlCodec.encodeRaw(
  "userName" -> "mike&dan",
  "age" -> 23
) // "userName=mike%26dan&age=23"

val rawDecoded = FormUrlCodec.decodeRaw(
  "userName=mike%26dan&age=23"
) // Vector("userName" -> "mike&dan", "age" -> "23")
```

## How to Test
rootJVM/test;rootJS/test

