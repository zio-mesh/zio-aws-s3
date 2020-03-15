## ZIO AWS S3 API Overview

All AWS methods are wrapped into a ZIO Task:

```scala
  type AwsTask[+A] = RIO[R, A]
```

1. Create an async S3 client
```scala
  def createClient(region: Region, endpoint: String): AwsTask[S3AsyncClient]
```

2. Create S3 bucket with the given name
```scala
  def createBucket(buck: String)(implicit s3: S3AsyncClient): AwsTask[CreateBucketResponse]
```

3. Delete the bucket with the given name
```scala
  def delBucket(buck: String)(implicit s3: S3AsyncClient): AwsTask[DeleteBucketResponse]
```

4. Obtain a list of all buckets owned by the authenticated sender
```scala
  def listBuckets(implicit s3: S3AsyncClient): AwsTask[ListBucketsResponse]
```

5. List all objects in a Bucket
```scala
  def listBucketObjects(buck: String, prefix: String)(implicit s3: S3AsyncClient): AwsTask[ListObjectsV2Response]
```

6. List all object keys in a Bucket
```scala
  def listObjectsKeys(buck: String, prefix: String)(implicit s3: S3AsyncClient): AwsTask[List[String]]
```

7. Look up for an object. True if present
```scala
  def lookupObject(buck: String, prefix: String, key: String)(implicit s3: S3AsyncClient): AwsTask[Boolean]
```

8. Setup redirection for a single object
```scala
def redirectObject(buck: String, prefix: String, key: String, url: String)
  (implicit s3: S3AsyncClient): AwsTask[CopyObjectResponse]
```

9. Setup redirection for all objects with a common prefix
```scala
def redirectPack(buck: String, prefix: String, url: String)(implicit s3: S3AsyncClient): AwsTask[Unit]
```

10. Copy object
```scala
def copyObject(buck: String, dstPrefix: String, srcKey: String, dstKey: String)
  (implicit s3: S3AsyncClient): AwsTask[CopyObjectResponse]
```

11. Put a file with a key into a Bucket
```scala
  def putObject(buck: String, key: String, file: String)(implicit s3: S3AsyncClient): AwsTask[PutObjectResponse]
```

12. Get a file with a key from a Bucket
```scala 
  def getObject(buck: String, key: String, file: String)(implicit s3: S3AsyncClient): AwsTask[GetObjectResponse]
```

13. Delete object by key from a Bucket
```scala
  def delObject(buck: String, key: String)(implicit s3: S3AsyncClient): AwsTask[DeleteObjectResponse]
```

14. Delete all objects in the bucket which share the same prefix
```scala
  def delAllObjects(buck: String, prefix: String)(implicit s3: S3AsyncClient): AwsTask[Unit]
```

15. Get current ACL settings
```scala
  def getObjectAcl(buck: String, key: String)(implicit s3: S3AsyncClient): AwsTask[GetObjectAclResponse]
```

15. Put new ACL settings
```scala
def putObjectAcl(buck: String, key: String, owner: Owner, grants: JList[Grant])
  (implicit s3: S3AsyncClient): AwsTask[PutObjectAclResponse]
```

16. Block all objects with ACL remove permission for a group of objects under the common prefix
```scala
  def blockPack(buck: String, prefix: String)(implicit s3: S3AsyncClient): AwsTask[Unit]
```

17. Unblock all objects with ACL remove permission for a group of objects under the common path
```scala 
  def unblockPack(buck: String, prefix: String)(implicit s3: S3AsyncClient): AwsTask[Unit]
```

18. Get ACL for each object in a path
```scala
  def getPackAcl(buck: String, prefix: String)(implicit s3: S3AsyncClient): AwsTask[List[GetObjectAclResponse]]
```

19. Put ACL for each object in a path
```scala
def putPackAcl(buck: String, prefix: String, block: Boolean)
  (implicit s3: S3AsyncClient): AwsTask[List[PutObjectAclResponse]]
```


