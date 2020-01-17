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

import java.nio.file.{ Paths }
import java.util.concurrent.CompletableFuture
import java.net.URI
import scala.jdk.CollectionConverters._

import zio.{ IO, Task }
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{
  AccessControlPolicy,
  CopyObjectRequest,
  CopyObjectResponse,
  CreateBucketRequest,
  CreateBucketResponse,
  DeleteBucketRequest,
  DeleteBucketResponse,
  DeleteObjectRequest,
  DeleteObjectResponse,
  GetObjectAclRequest,
  GetObjectAclResponse,
  GetObjectRequest,
  GetObjectResponse,
  ListBucketsResponse,
  ListObjectsV2Request,
  ListObjectsV2Response,
  PutObjectAclRequest,
  PutObjectAclResponse,
  PutObjectRequest,
  PutObjectResponse
}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AwsLink extends GenericLink {

  val service = new GenericLink.Service[Any] {
    def createClient(region: Region, endpoint: String): Task[S3AsyncClient] = {
      val client =
        if (endpoint.isEmpty())
          S3AsyncClient
            .builder()
            .region(region)
            .build()
        else
          S3AsyncClient
            .builder()
            .region(region)
            .endpointOverride(URI.create(endpoint))
            .build()
      Task(client)
    }

    def createBucket(buck: String)(implicit s3: S3AsyncClient): Task[CreateBucketResponse] =
      IO.effectAsync[Throwable, CreateBucketResponse] { callback =>
        processResponse(
          s3.createBucket(CreateBucketRequest.builder().bucket(buck).build()),
          callback
        )
      }

    def delBucket(buck: String)(implicit s3: S3AsyncClient): Task[DeleteBucketResponse] =
      IO.effectAsync[Throwable, DeleteBucketResponse] { callback =>
        processResponse(
          s3.deleteBucket(DeleteBucketRequest.builder().bucket(buck).build()),
          callback
        )
      }

    def listBuckets(implicit s3: S3AsyncClient): Task[ListBucketsResponse] =
      IO.effectAsync[Throwable, ListBucketsResponse] { callback =>
        processResponse(s3.listBuckets(), callback)
      }

    def listBucketObjects(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[ListObjectsV2Response] =
      for {
        resp <- IO.effect(
                 s3.listObjectsV2(
                   ListObjectsV2Request
                     .builder()
                     .bucket(buck)
                     //  .maxKeys(10)
                     .prefix(prefix)
                     .build()
                 )
               )
        list <- IO.effectAsync[Throwable, ListObjectsV2Response] { callback =>
                 processResponse(
                   resp,
                   callback
                 )
               }
      } yield list

    def listObjectsKeys(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[List[String]] =
      for {
        list <- listBucketObjects(buck, prefix)
        keys = list.contents.asScala.map(_.key).toList
        _    = println(s">>>>>> Total keys found for prefix ${prefix}: ${keys.size}")
      } yield keys

    def lookupObject(buck: String, prefix: String, key: String)(implicit s3: S3AsyncClient): Task[Boolean] =
      for {
        list   <- listBucketObjects(buck, prefix)
        newKey = prefix + "/" + key
        res = list.contents.asScala
          .filter(_.key == newKey)
          .nonEmpty
      } yield res

    def getObjectAcl(buck: String, key: String)(implicit s3: S3AsyncClient): Task[GetObjectAclResponse] = {
      val req = GetObjectAclRequest.builder.bucket(buck).key(key).build

      println(s">>>>> Get ACL for key: ${key}")

      IO.effectAsync[Throwable, GetObjectAclResponse] { callback =>
          processResponse(s3.getObjectAcl(req), callback)
        }
        .mapError(_ => new Throwable("Failed Processing CopyObjectResponse"))
    }

    def putObjectAcl(buck: String, key: String)(implicit s3: S3AsyncClient): Task[PutObjectAclResponse] =
      for {
        curr <- getObjectAcl(buck, key)
        grants = curr.grants.asScala
          .drop(1)
          .asJava // drop only the second and later Grants. grants.head is the owner and should be preserved!!!
        // _      = println(s">>>>>>>>> Grants ${grants}")
        acl <- Task.effect(AccessControlPolicy.builder.owner(curr.owner).grants(grants).build())
        req <- Task.effect(PutObjectAclRequest.builder().bucket(buck).key(key).accessControlPolicy(acl).build())
        rsp <- IO
                .effectAsync[Throwable, PutObjectAclResponse] { callback =>
                  processResponse(s3.putObjectAcl(req), callback)
                }
                .mapError(_ => new Throwable("Failed Processing PutObjectAclResponse"))
      } yield rsp

    def redirectPack(buck: String, prefix: String, url: String)(implicit s3: S3AsyncClient): Task[Unit] =
      for {
        keys <- listObjectsKeys(buck, prefix)
        _    = Task.traverse(keys)(key => redirectObject(buck, prefix, key, url))
      } yield ()

    def blockPack(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[Unit] =
      for {
        keys <- listObjectsKeys(buck, prefix)
        _    = Task.traverse(keys)(key => getObjectAcl(buck, key))
      } yield ()

    def redirectObject(buck: String, prefix: String, key: String, url: String)(
      implicit s3: S3AsyncClient
    ): Task[CopyObjectResponse] = {
      val dstPrefix = prefix + "/" + url
      copyObject(buck, dstPrefix, key, key) // copy each object inside the same buck, but with diff indexes
    }

    def copyObject(buck: String, dstPrefix: String, srcKey: String, dstKey: String)(
      implicit s3: S3AsyncClient
    ): Task[CopyObjectResponse] = {

      val src = URLEncoder.encode(buck + "/" + srcKey, StandardCharsets.UTF_8.toString)
      val dst = URLEncoder.encode(buck + "/" + dstKey, StandardCharsets.UTF_8.toString)

      println(s">>>>>> Copy Src Link: ${src}")
      println(s">>>>>> Copy Dst link: ${dst}")

      for {
        req <- IO.effect(
                CopyObjectRequest
                  .builder()
                  .destinationBucket(buck)
                  .destinationKey(dstKey)
                  .copySource(src)
                  .websiteRedirectLocation(dst)
                  .build()
              )
        rsp <- IO
                .effectAsync[Throwable, CopyObjectResponse] { callback =>
                  processResponse(s3.copyObject(req), callback)
                }
                .mapError(_ => new Throwable("Failed Processing CopyObjectResponse"))
      } yield rsp
    }

    def putObject(buck: String, key: String, file: String)(implicit s3: S3AsyncClient): Task[PutObjectResponse] =
      IO.effectAsync[Throwable, PutObjectResponse] { callback =>
        processResponse(
          s3.putObject(PutObjectRequest.builder().bucket(buck).key(key).build(), Paths.get(file)),
          callback
        )
      }

    def getObject(buck: String, key: String, file: String)(implicit s3: S3AsyncClient): Task[GetObjectResponse] =
      IO.effectAsync[Throwable, GetObjectResponse] { callback =>
        processResponse(
          s3.getObject(GetObjectRequest.builder().bucket(buck).key(key).build(), Paths.get(file)),
          callback
        )
      }

    def delObject(buck: String, key: String)(implicit s3: S3AsyncClient): Task[DeleteObjectResponse] =
      IO.effectAsync[Throwable, DeleteObjectResponse] { callback =>
        processResponse(
          s3.deleteObject(DeleteObjectRequest.builder().bucket(buck).key(key).build()),
          callback
        )
      }

    def delAllObjects(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[Unit] =
      for {
        keys <- listObjectsKeys(buck, prefix)
        _ <- Task.traverse(keys) { i =>
              delObject(buck, i)
            }
      } yield ()

    def processResponse[T](
      fut: CompletableFuture[T],
      callback: Task[T] => Unit
    ): Unit =
      fut.handle[Unit]((response, err) => {
        err match {
          case null => callback(IO.succeed(response))
          case ex   => callback(IO.fail(ex))
        }
      }): Unit
  }
}
