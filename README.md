# zio-aws-s3

Welcome to ZIO AWS S3 !

This project aims to ease ZIO integration with AWS S3, providing a clean, simple and efficient API.

### Major features

* AWS S3 Java v2 2.10.87
* ZIO RC-18-2
* ZIO Task wrapper for all AWS methods 
* [Module Pattern with ZIO Layer](https://zio.dev/docs/howto/howto_use_layers) implementation 
* [ZIO Test](https://zio.dev/docs/howto/howto_test_effects) integration
* Support for Scala 2.13.1 and 2.12.10

### Integration 
`libraryDependencies += "io.github.neurodyne" %% "zio-aws-s3 % 0.4.12`


### API Reference
1. See [[API Refenrece]](docs/Api.md)
2. See [[usage]](docs/Basic.md)

  
### Integration tests
Check ZTest [specs](https://github.com/Neurodyne/zio-aws-s3/blob/master/src/test/scala/BaseSpec.scala) for more details
  
### Resources 
1. AWS SDK Java v2 [[Documentation]](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html)
2. AWS SDK Java v2 [[Repo]](https://github.com/aws/aws-sdk-java-v2)
