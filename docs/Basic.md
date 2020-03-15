## API Basic Usage

```scala
testM("list all keys, related to a specific prefix") {      
  val res = for {
    s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
    out <- aws.service.listObjectsKeys(bucket, prefix)(s3)
    _   = println(out)
  } yield out

  assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")), equalTo("ok"))
  } @@ timeout(10.seconds)
)

object setup {

  val env      = System.getenv()
  val endpoint = env.get("AWS_ENDPOINT")
  val bucket   = env.get("AWS_BUCKET")
  val region   = software.amazon.awssdk.regions.Region.US_EAST_1

  val prefix  = "media/uploads/images/cf3a53e4-37bd-11ea-b430-6f9a089d05d1"
}

  ```