/*
 * Copyright 2020 hot.crew
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

// /*
//  * Copyright 2020 hot.crew
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *     http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */

// package hot.crew.s3

// import software.amazon.awssdk.services.s3.S3AsyncClient

// import java.nio.file.Paths
// import java.util.concurrent.CompletableFuture

// import scala.jdk.CollectionConverters._

// import com.github.ghik.silencer.silent
// import zio.{ Has, IO, RLayer, Task, URLayer, ZIO, ZLayer }
// import software.amazon.awssdk.regions.Region
// import software.amazon.awssdk.services.s3.S3AsyncClient
// import software.amazon.awssdk.services.s3.model.{
//   AccessControlPolicy,
//   CopyObjectRequest,
//   CopyObjectResponse,
//   CreateBucketRequest,
//   CreateBucketResponse,
//   DeleteBucketRequest,
//   DeleteBucketResponse,
//   DeleteObjectRequest,
//   DeleteObjectResponse,
//   GetObjectAclRequest,
//   GetObjectAclResponse,
//   GetObjectRequest,
//   GetObjectResponse,
//   Grant,
//   Grantee,
//   ListBucketsResponse,
//   ListObjectsV2Request,
//   ListObjectsV2Response,
//   Owner,
//   Permission,
//   PutObjectAclRequest,
//   PutObjectAclResponse,
//   PutObjectRequest,
//   PutObjectResponse,
//   Type
// }
// import java.net.URLEncoder
// import java.nio.charset.StandardCharsets
// import java.util.{ List => JList }
// import java.net.URI

// import software.amazon.awssdk.auth.credentials.{ AwsCredentials, StaticCredentialsProvider }
// import software.amazon.awssdk.core.async.AsyncResponseTransformer

// object AwsAgent {
//   def createClient(region: Region, endpointOverride: Option[String] = None): Task[S3AsyncClient] = Task {
//     val initBuilder = S3AsyncClient.builder.region(region)
//     endpointOverride
//       .map(URI.create)
//       .map(initBuilder.endpointOverride)
//       .getOrElse(initBuilder)
//       .build
//   }

//   def createClientWithCreds(
//     region: Region,
//     creds: AwsCredentials,
//     endpointOverride: Option[String] = None
//   ): Task[S3AsyncClient] = Task {
//     val initBuilder = S3AsyncClient.builder.region(region).credentialsProvider(StaticCredentialsProvider.create(creds))
//     endpointOverride
//       .map(URI.create)
//       .map(initBuilder.endpointOverride)
//       .getOrElse(initBuilder)
//       .build
//   }
// }

// class LiveLink(s3: S3AsyncClient) extends GenericLink {
//   def createBucket(buck: String): Task[CreateBucketResponse] =
//     IO.effectAsync[Throwable, CreateBucketResponse] { callback =>
//       processResponse(
//         s3.createBucket(CreateBucketRequest.builder.bucket(buck).build),
//         callback
//       )
//     }

//   def delBucket(buck: String): Task[DeleteBucketResponse] =
//     IO.effectAsync[Throwable, DeleteBucketResponse] { callback =>
//       processResponse(
//         s3.deleteBucket(DeleteBucketRequest.builder.bucket(buck).build),
//         callback
//       )
//     }

//   def listBuckets: Task[ListBucketsResponse] =
//     IO.effectAsync[Throwable, ListBucketsResponse](callback => processResponse(s3.listBuckets, callback))

//   def listBucketObjects(buck: String, prefix: String): Task[ListObjectsV2Response] =
//     for {
//       resp <- IO.effect(
//                 s3.listObjectsV2(
//                   ListObjectsV2Request.builder
//                     .bucket(buck)
//                     .maxKeys(20)
//                     .prefix(prefix)
//                     .build
//                 )
//               )
//       list <- IO.effectAsync[Throwable, ListObjectsV2Response] { callback =>
//                 processResponse(
//                   resp,
//                   callback
//                 )
//               }
//     } yield list

//   def listObjectsKeys(buck: String, prefix: String): Task[List[String]] =
//     for {
//       list <- listBucketObjects(buck, prefix)
//       keys  = list.contents.asScala.map(_.key).toList
//     } yield keys

//   def delObject(buck: String, key: String): Task[DeleteObjectResponse] =
//     IO.effectAsync[Throwable, DeleteObjectResponse] { callback =>
//       processResponse(
//         s3.deleteObject(DeleteObjectRequest.builder.bucket(buck).key(key).build),
//         callback
//       )
//     }

//   def delAllObjects(buck: String, prefix: String): Task[Unit] =
//     for {
//       keys <- listObjectsKeys(buck, prefix)
//       _    <- Task.foreach(keys)(key => delObject(buck, key))
//     } yield ()

//   private def processResponse[T](fut: CompletableFuture[T], callback: Task[T] => Unit): Unit = {
//     fut.handle[Unit] { (response, err) =>
//       err match {
//         case null => callback(IO.succeed(response))
//         case ex   => callback(IO.fail(ex))
//       }
//     }
//     ()
//   }
// }
