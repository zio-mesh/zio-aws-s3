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

import zio.{ Task }
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{
  CopyObjectResponse,
  CreateBucketResponse,
  DeleteBucketResponse,
  DeleteObjectResponse,
  GetObjectResponse,
  ListBucketsResponse,
  ListObjectsV2Response,
  PutObjectResponse
}

trait GenericLink {
  val service: GenericLink.Service[Any]
}

object GenericLink {
  trait Service[R] {

    /**
     *
     * Bucket API
     *
     */
    /**
     * Create an async S3 client.
     */
    def createClient(region: Region, endpoint: String): Task[S3AsyncClient]

    /**
     * Create S3 bucket with the given name.
     */
    def createBucket(buck: String)(implicit s3: S3AsyncClient): Task[CreateBucketResponse]

    /**
     * Delete the bucket with the given name.
     */
    def delBucket(buck: String)(implicit s3: S3AsyncClient): Task[DeleteBucketResponse]

    /**
     * Obtain a list of all buckets owned by the authenticated sender.
     */
    def listBuckets(implicit s3: S3AsyncClient): Task[ListBucketsResponse]

    /**
     *
     * Object API
     *
     */
    /**
     * List all objects in a Bucket
     */
    def listBucketObjects(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[ListObjectsV2Response]

    /**
     * List all object keys in a Bucket
     */
    def listObjectsKeys(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[List[String]]

    /**
     * Look up for an object. True if present
     */
    def lookupObject(buck: String, prefix: String, key: String)(implicit s3: S3AsyncClient): Task[Boolean]

    /**
     * Setup redirection for all objects with a prefix
     */
    def redirectObject(buck: String, prefix: String, key: String, url: String)(
      implicit s3: S3AsyncClient
    ): Task[CopyObjectResponse]

    /**
     * Put a file with a key into a Bucket
     */
    def putObject(buck: String, key: String, file: String)(implicit s3: S3AsyncClient): Task[PutObjectResponse]

    /**
     * Get a file with a key from a Bucket
     */
    def getObject(buck: String, key: String, file: String)(implicit s3: S3AsyncClient): Task[GetObjectResponse]

    /**
     * Delete object by key from a Bucket
     */
    def delObject(buck: String, key: String)(implicit s3: S3AsyncClient): Task[DeleteObjectResponse]

    /**
     * Delete all objects
     */
    def delAllObjects(buck: String, prefix: String)(implicit s3: S3AsyncClient): Task[Unit]

  }
}
