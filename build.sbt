import Versions._

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

inThisBuild(
  List(
    scalaVersion := "2.12.10",
    organization := "io.github.neurodyne",
    homepage := Some(url("http://neurodyne.pro")),
    organizationName := "Neurodyne Systems",
    homepage := Some(url("https://github.com/Neurodyne/zio-aws-s3")),
    startYear := Some(2019),
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
      ScmInfo(url("https://github.com/Neurodyne/zio-aws-s3"), "scm:git@github.com:Neurodyne/zio-aws-s3.git")
    )
  )
)

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val root =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(commonSettings)
    .settings(
      commonDeps,
      zioDeps,
      awsDeps
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val awsDeps = libraryDependencies ++= Seq("software.amazon.awssdk" % "s3" % awsVersion)

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % zioVersion,
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
)

lazy val commonDeps = libraryDependencies ++= Seq(
  compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
)

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    name := "zio-aws-s3",
    version := "0.4.12",
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

lazy val docs = project // new documentation project
  .in(file("zio-aws-s3-docs"))
  .settings(
    skip.in(publish) := true,
    moduleName := "zio-aws-s3-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion
    )
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin)

publishTo := sonatypePublishToBundle.value

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "compile")
addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("pub", "; publishSigned; sonatypeBundleRelease")
