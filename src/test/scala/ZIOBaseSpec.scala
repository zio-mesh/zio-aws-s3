package zio_aws_s3

import zio.duration._
import zio.test._

trait ZIOBaseSpec extends DefaultRunnableSpec {
  override def aspects = List(TestAspect.timeout(10.seconds))
}
