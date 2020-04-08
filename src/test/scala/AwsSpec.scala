/*
 * Copyright 2019 io.github.neurodyne
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
import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect.ignore

import java.io.IOException
import setup._
import Helper._

object BuckSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      suite("Buck Spec")(
        testM("list all buckets") {
          println(s"Using Region: ${region} and Endpoint: ${endpoint}")

          val res = for {
            s3   <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            prog <- AwsApp.listBuckets.provideLayer(topEnv).provide(s3)
          } yield prog

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        }
      )
    )
}

object ObjSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      suite("Object Spec")(
        testM("lookup an object. True if present") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.lookupObject(bucket, prefix, key).provideLayer(topEnv).provide(s3)
            _   = println(s"Found objects: ${out}")
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        },
        testM("list all keys, related to a specific prefix") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.listObjectsKeys(bucket, prefix).provideLayer(topEnv).provide(s3)
            _   = println(out)
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        }
      )
    )
}

object DelSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      suite("Delete Spec")(
        testM("list and delete object") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.listObjectsKeys(bucket, prefix).provideLayer(topEnv).provide(s3)
            _   = println(out)
            _   = println("**** Carefully ! This method will ACTUALLY remove your AWS content !!!! ***")
            _   = println("*** If you REALLY wanna remove it, uncomment the line below ***")
            _   <- AwsApp.delAllObjects(bucket, prefix).provideLayer(topEnv).provide(s3)

          } yield ()

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        }
      )
    )
}

object RedirSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      suite("Redirection Spec")(
        testM("set a single object redirection") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.redirectObject(bucket, prefix, fullKey, url).provideLayer(topEnv).provide(s3)
            _   = println(out)
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        } @@ ignore,
        testM("set a multiple object redirection with a single prefix") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.redirectPack(bucket, prefix, url).provideLayer(topEnv).provide(s3)
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        }
      )
    )
}

object AclSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      suite("ACL Spec")(
        testM("get pack ACL") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.getPackAcl(bucket, prefix).provideLayer(topEnv).provide(s3)
            _   = println(out)
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        },
        testM("set pack ACL") {

          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.putPackAcl(bucket, prefix, false).provideLayer(topEnv).provide(s3)
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        } @@ ignore
      )
    )
}

object BlockSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      suite("Block Spec")(
        testM("block content pack by removing ACL grant") {
          val res = for {
            s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
            out <- AwsApp.blockPack(bucket, prefix).provideLayer(topEnv).provide(s3)
          } yield out

          assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
        }
      )
    )
}

object UnBlockSpec extends BaseSpec {
  def spec =
    suite("AwsSpec")(
      testM("unblock content pack by adding ACL grant") {
        val res = for {
          s3  <- AwsAgent.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
          out <- AwsApp.unblockPack(bucket, prefix).provideLayer(topEnv).provide(s3)
        } yield out

        assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
      }
    )
}

object Helper {
  import java.nio.file.{ Files }
  import java.io.File

  def createOutFile(dir: String = "./", file: String = "outfile"): File = {
    val outDir = Files.createTempDirectory(dir)
    val path   = outDir.resolve(file)
    println("File to create path: " + path)
    Files.createFile(path).toFile
  }

  val key     = "42x42.jpg"
  val url     = "backup"
  val prefix  = "media/uploads/images/b6796028-7987-11ea-8882-6f295c6d861c"
  val fullKey = prefix + "/" + key

  // Build a layered env
  val topEnv = AwsApp.ExtDeps.live >>> AwsApp.AwsLink.live

}
