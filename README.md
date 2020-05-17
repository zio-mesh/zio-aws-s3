[![Maven][mavenImg]][mavenLink]

[mavenImg]: https://img.shields.io/maven-central/v/io.github.neurodyne/zio-aws-s3_2.13.svg
[mavenLink]: https://mvnrepository.com/artifact/io.github.neurodyne/zio-aws-s3

# zio-aws-s3

Welcome to ZIO AWS S3 !

This project aims to ease ZIO integration with AWS S3, providing a clean, simple and efficient API.

## Features

* AWS S3 Java v2 2.10.87
* ZIO RC-18-2
* ZIO Module wrapper for all AWS methods
* [Module Pattern with ZIO Layer](https://zio.dev/docs/howto/howto_use_layers) implementation
* [ZIO Test](https://zio.dev/docs/howto/howto_test_effects) integration
* Support for Scala 2.13.1 and 2.12.10

## Integration

```scala
"io.github.neurodyne" %% "zio-aws-s3" % version
```

```scala
ivy"io.github.neurodyne::zio-aws-s3:$version"
```

## Getting Started

```scala
// build.sbt
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "1.0.0-RC19",
  "io.github.neurodyne" %% "zio-aws-s3" % "0.4.12"
)

// build.sc
def ivyDeps = Agg(
  ivy"dev.zio::zio:1.0.0-RC19",
  ivy"io.github.neurodyne::zio-aws-s3:0.4.12"
)
```

```scala
import zio._
import zio_aws_s3.{ AwsAgent, AwsApp }
import software.amazon.awssdk.regions.Region

import scala.jdk.CollectionConverters._

object Main extends App {
  val BUCKET = "<your-bucket>"

  val awsEnv = AwsApp.ExtDeps.live >>> AwsApp.AwsLink.live

  val app = for {
    s3 <- AwsAgent.createClient(Region.US_WEST_2)

    response <- AwsApp.listBuckets.provideLayer(awsEnv).provide(s3)

    buckets <- Task(response.buckets.asScala.toList.map(_.name))

    _ = buckets.foreach(println)
  } yield ()

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    app.fold(_ => 1, _ => 0)
  }
}
```

## API Reference

1. See [[API Reference]](docs/Api.md)

## Running Tests

All the tests are integration tests. That is, they make real API requests to S3. As such, you'll need to make sure you have variables set to a bucket and object that you can access and manipulate.

Here are all the things you will need to change to run the tests locally:

1) Export `AWS_BUCKET` as an environment variable in the window you will be running the tests.
2) Change the `region` in `Setup.scala` to your region.
3) Change the `prefix` and `key` in `AwsSpec.scala` to a prefix/key in your bucket.

Then:

```bash
> sbt
> testOnly *BuckSpec
```

## Commercial integration and support services are available

Please open an issue with your commercial integration request
  
## Resources

1. AWS SDK Java v2 [[Documentation]](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html)
2. AWS SDK Java v2 [[Repo]](https://github.com/aws/aws-sdk-java-v2)
