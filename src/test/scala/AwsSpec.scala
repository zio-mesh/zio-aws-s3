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
import zio.test._
import zio.test.Assertion._

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

// object ObjSpec extends ZIOBaseSpec {
//   def spec =
//     suite("AwsSpec")(
//       suite("blah")(
//         testM("lookup an object. True if present") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.lookupObject(bucket, prefix, key)(s3)
//             _   = println(s"Found objects: ${out}")
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         },
//         testM("list all keys, related to a specific prefix") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.listObjectsKeys(bucket, prefix)(s3)
//             _   = println(out)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         }
//       )
//     )
// }

// object DelSpec extends ZIOBaseSpec {
//   def spec =
//     suite("AwsSpec")(
//       suite("blah")(
//         testM("list and delete object") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.listObjectsKeys(bucket, prefix)(s3)
//             _   = println(out)
//             _   = println("**** Carefully ! This method will ACTUALLY remove your AWS content !!!! ***")
//             _   = println("*** If you REALLY wanna remove it, uncomment the line below ***")
//             // _   <- aws.service.delAllObjects(bucket, prefix)(s3)

//           } yield ()

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         }
//       )
//     )
// }

// object RedirSpec extends ZIOBaseSpec {
//   def spec =
//     suite("AwsSpec")(
//       suite("blah")(
//         testM("set a single object redirection") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.redirectObject(bucket, prefix, fullKey, url)(s3)
//             _   = println(out)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         } @@ ignore,
//         testM("set a multiple object redirection with a single prefix") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.redirectPack(bucket, prefix, url)(s3)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         }
//       )
//     )
// }

// object AclSpec extends ZIOBaseSpec {
//   def spec =
//     suite("AwsSpec")(
//       suite("blah")(
//         testM("get pack ACL") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.getPackAcl(bucket, prefix)(s3)
//             _   = println(out)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         },
//         testM("set pack ACL") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.putPackAcl(bucket, prefix, false)(s3)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         } @@ ignore
//       )
//     )
// }

// object BlockSpec extends ZIOBaseSpec {
//   def spec =
//     suite("AwsSpec")(
//       suite("blah")(
//         testM("block content pack by removing ACL grant") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.blockPack(bucket, prefix)(s3)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         } @@ ignore,
//         testM("unblock content pack by adding ACL grant") {
//           val res = for {
//             s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
//             out <- aws.service.unblockPack(bucket, prefix)(s3)
//           } yield out

//           assertM(res.foldM(_ => ZIO.fail("failed"), _ => ZIO.succeed("ok")))(equalTo("ok"))
//         }
//       )
//     )
// }

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
  val prefix  = "media/uploads/images/cf3a53e4-37bd-11ea-b430-6f9a089d05d1"
  val fullKey = prefix + "/" + key

  // Build a layered env
  val topEnv = AwsApp.ExtDeps.live >>> AwsApp.TempLink.live

}
