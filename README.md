# zio-aws-s3

Welcome to ZIO AWS S3 !

This project aims to ease ZIO integration with AWS S3, providing a clean, simple and efficient API.

### Major features
* AWS S3 Java v2 2.10.42
* ZIO Task wrapper for all AWS methods 
* [Module Pattern](https://zio.dev/docs/howto/howto_use_module_pattern) implementation 
* [ZIO Test](https://zio.dev/docs/howto/howto_test_effects) integration
* Support for Scala 2.13.1 and 2.12.10

### Usage
With ZIO Test library, you can use this as simple as:
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
  ```
  
  ### Check ZTest [specs](https://github.com/Neurodyne/zio-aws-s3/blob/master/src/test/scala/BaseSpec.scala) for more details
  
