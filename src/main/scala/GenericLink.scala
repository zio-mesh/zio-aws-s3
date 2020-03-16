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

import zio.{ RIO }
// import software.amazon.awssdk.regions.Region
// import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{
  CopyObjectResponse,
  CreateBucketResponse,
  DeleteBucketResponse,
  DeleteObjectResponse,
  GetObjectAclResponse,
  GetObjectResponse,
  Grant,
  ListBucketsResponse,
  ListObjectsV2Response,
  Owner,
  PutObjectAclResponse,
  PutObjectResponse
}

import java.util.{ List => JList }

trait GenericLink[R] {
  type AwsTask[+A] = RIO[R, A]

  /**
   *
   * Bucket API
   *
   */
  /**
   * Create an async S3 client.
   */
  // def createClient(region: Region, endpoint: String): AwsTask[S3AsyncClient]

  /**
   * Create S3 bucket with the given name.
   */
  def createBucket(buck: String): AwsTask[CreateBucketResponse]

  /**
   * Delete the bucket with the given name.
   */
  def delBucket(buck: String): AwsTask[DeleteBucketResponse]

  /**
   * Obtain a list of all buckets owned by the authenticated sender.
   */
  def listBuckets: AwsTask[ListBucketsResponse]

  /**
   *
   * Object API
   *
   */
  /**
   * List all objects in a Bucket
   */
  def listBucketObjects(buck: String, prefix: String): AwsTask[ListObjectsV2Response]

  /**
   * List all object keys in a Bucket
   */
  def listObjectsKeys(buck: String, prefix: String): AwsTask[List[String]]

  /**
   * Look up for an object. True if present
   */
  def lookupObject(buck: String, prefix: String, key: String): AwsTask[Boolean]

  /**
   * Setup redirection for a single object
   */
  def redirectObject(buck: String, prefix: String, key: String, url: String): AwsTask[CopyObjectResponse]

  /**
   * Setup redirection for all objects with a common prefix
   */
  def redirectPack(buck: String, prefix: String, url: String): AwsTask[Unit]

  /**
   * Copy an object
   */
  def copyObject(buck: String, dstPrefix: String, srcKey: String, dstKey: String): AwsTask[CopyObjectResponse]

  /**
   * Put a file with a key into a Bucket
   */
  def putObject(buck: String, key: String, file: String): AwsTask[PutObjectResponse]

  /**
   * Get a file with a key from a Bucket
   */
  def getObject(buck: String, key: String, file: String): AwsTask[GetObjectResponse]

  /**
   * Delete object by key from a Bucket
   */
  def delObject(buck: String, key: String): AwsTask[DeleteObjectResponse]

  /**
   * Delete all objects which share the same prefix
   */
  def delAllObjects(buck: String, prefix: String): AwsTask[Unit]

  /**
   * Get current ACL settings
   */
  def getObjectAcl(buck: String, key: String): AwsTask[GetObjectAclResponse]

  /**
   * Put new ACL settings
   */
  def putObjectAcl(buck: String, key: String, owner: Owner, grants: JList[Grant]): AwsTask[PutObjectAclResponse]

  /**
   * Block all objects with ACL remove permission for a group of objects under the common path
   */
  def blockPack(buck: String, prefix: String): AwsTask[Unit]

  /**
   * Unblock all objects with ACL remove permission for a group of objects under the common path
   */
  def unblockPack(buck: String, prefix: String): AwsTask[Unit]

  /**
   * Get ACL for each object in a path
   */
  def getPackAcl(buck: String, prefix: String): AwsTask[List[GetObjectAclResponse]]

  /**
   * Put ACL for each object in a path
   */
  def putPackAcl(buck: String, prefix: String, block: Boolean): AwsTask[List[PutObjectAclResponse]]

}
