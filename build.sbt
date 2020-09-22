resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.jcenterRepo
)

inThisBuild(
  List(
    scalaVersion := "2.13.3",
    crossScalaVersions := Seq("2.12.12", "2.13.3"),
    organization := "zio.crew",
    homepage := Some(url("https://github.com/hot-crew/zio-aws-s3")),
    startYear := Some(2020),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "tampler",
        "Boris V.Kuznetsov",
        "socnetfpga@gmail.com",
        url("https://github.com/tampler")
      )
    ),
    scmInfo := Some(
      ScmInfo(url("https://github.com/hot-crew/zio-aws-s3"), "scm:git@github.com:hot-crew/zio-aws-s3.git")
    )
  )
)

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val core =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(commonSettings, pubSettings)
    .settings(
      commonDeps,
      zioDeps,
      awsDeps
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val awsDeps = libraryDependencies ++= Seq("software.amazon.awssdk" % "s3" % Version.aws)

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % Version.zio,
  "dev.zio" %% "zio-test"     % Version.zio % "test",
  "dev.zio" %% "zio-test-sbt" % Version.zio % "test"
)

lazy val commonDeps = libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.2.0",
  compilerPlugin("com.github.ghik" % "silencer-plugin" % Version.silencer cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % Version.silencer % Provided cross CrossVersion.full
)

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    scalacOptions -= "-Xfatal-warnings",
    name := "zio-aws-s3",
    version := "0.4.17",
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

lazy val pubSettings = Seq(
  bintrayRepository := "zio-aws-s3",
  publishMavenStyle := true,
  bintrayOrganization := Some("hot-crew")
)

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "compile")
addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
