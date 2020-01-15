/*
 * Copyright 2019 Neurodyne Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio_aws_s3

import zio.{ ZIO }
import zio.duration._
import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._

import java.io.IOException
import Helper._

object Tests {

  val bucketsSuite = suite("AWS S3 Buckets suite")(
    testM("list all buckets") {
      println(s"Using Region: ${region} and Endpoint: ${endpoint}")
      val res = for {
        s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
        out <- aws.service.listBuckets(s3)
        _   = println(out)
      } yield out

      assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")), equalTo("ok"))
    } @@ timeout(10.seconds)
  )

  val objectsSuite = suite("AWS S3 Objects suite")(
    testM("lookup an object. True if present") {
      println(s"Using Region: ${region} and Endpoint: ${endpoint}")
      val res = for {
        s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
        out <- aws.service.lookupObject(bucket, prefix, key)(s3)
        _   = println(s"Found objects: ${out}")
      } yield out

      assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")), equalTo("ok"))
    } @@ timeout(10.seconds),
    testM("list all keys, related to a specific prefix") {
      println(s"Using Region: ${region}, Endpoint: ${endpoint}, Bucket: ${bucket}")
      val res = for {
        s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
        out <- aws.service.listObjectsKeys(bucket, prefix)(s3)
        _   = println(out)
      } yield out

      assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")), equalTo("ok"))
    } @@ timeout(10.seconds)
  )
  val delSuite = suite("Delete suite")(
    testM("list and delete object") {
      println(s"Using Region: ${region} and Endpoint: ${endpoint}")
      val res = for {
        s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
        out <- aws.service.listObjectsKeys(bucket, prefix)(s3)
        _   = println(out)
        _   = println("**** Carefully ! This method will ACTUALLY remove your AWS content !!!! ***")
        _   = println("*** If you REALLY wanna remove it, uncomment the line below ***")
        // _   <- aws.service.delAllObjects(bucket, prefix)(s3)

      } yield ()

      assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")), equalTo("ok"))
    } @@ timeout(10.seconds)
  )

  val redirSuite = suite("Redirection suite")(
    testM("set object redirection") {
      println(s"Using Region: ${region} and Endpoint: ${endpoint}")
      val res = for {
        s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
        out <- aws.service.redirectObject(bucket, prefix, key, url)(s3)
        _   = println(out)
      } yield out

      assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")), equalTo("ok"))
    } @@ timeout(10.seconds)
  )

}

object BuckSpec  extends DefaultRunnableSpec(suite("Bucket Spec")(Tests.bucketsSuite))
object ObjSpec   extends DefaultRunnableSpec(suite("Object Spec")(Tests.objectsSuite))
object RedirSpec extends DefaultRunnableSpec(suite("Redirection Spec")(Tests.redirSuite))
object DelSpec   extends DefaultRunnableSpec(suite("Redirection Spec")(Tests.delSuite))

object Helper {
  import java.nio.file.{ Files }
  import java.io.File

  import software.amazon.awssdk.regions.Region

  val region: Region = Region.US_EAST_1
  val env            = System.getenv()
  val endpoint       = env.get("AWS_ENDPOINT")
  val bucket         = env.get("AWS_BUCKET")

  def createOutFile(dir: String = "./", file: String = "outfile"): File = {
    val outDir = Files.createTempDirectory(dir)
    val path   = outDir.resolve(file)
    println("File to create path: " + path)
    Files.createFile(path).toFile

  }

  val aws    = new AwsLink {}
  val key    = "42x42.jpg"
  val url    = "redirected"
  val prefix = "media/uploads/images/ee912008-2e38-11ea-89d3-45d08ddd3995"

}
