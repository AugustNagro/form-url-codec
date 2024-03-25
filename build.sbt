ThisBuild / organization := "com.augustnagro"
ThisBuild / version := "0.2.0-SNAPSHOT"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / scalaVersion := "3.3.0"
ThisBuild / scalacOptions ++= Seq("-deprecation")
ThisBuild / homepage := Some(
  url("https://github.com/AugustNagro/form-url-codec")
)
ThisBuild / licenses += ("Apache-2.0", url(
  "https://opensource.org/licenses/Apache-2.0"
))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/AugustNagro/form-url-codec"),
    "scm:git:git@github.com:AugustNagro/form-url-codec.git",
    Some("scm:git:git@github.com:AugustNagro/form-url-codec.git")
  )
)
ThisBuild / developers := List(
  Developer(
    id = "augustnagro@gmail.com",
    name = "August Nagro",
    email = "augustnagro@gmail.com",
    url = url("https://augustnagro.com")
  )
)
ThisBuild / publishMavenStyle := true
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
ThisBuild / publish / skip := true

lazy val root = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(
    name := "form-url-codec",
    libraryDependencies += "org.scalameta" %%% "munit" % "0.7.29" % Test
  )
  .jsSettings(
    publish / skip := false
  )
  .jvmSettings(
    publish / skip := false
  )
