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

import zio.{ Runtime, Task }
import zio.console.putStrLn
import setup._
import java.io.IOException
import software.amazon.awssdk.services.s3.S3AsyncClient

import TempApp.{ TempLink }
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData

object App0 extends App {

  val rt     = Runtime.default
  val prefix = "media/uploads/images/cf3a53e4-37bd-11ea-b430-6f9a089d05d1"
  // aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))

  // val aws = new AwsLink {}

  // val res = for {
  //   s3  <- aws.service.createClient(region, endpoint).mapError(_ => new IOException("S3 client creation failed"))
  //   out <- aws.service.listObjectsKeys(bucket, prefix)
  //   _   = println(out)
  // } yield out
  def buildClient(): Task[S3AsyncClient] = ???

  // val env = ExtDeps.live >>>

  val prog = for {
    s3   <- buildClient()
    deps = ???
    buck <- TempLink.live.provideLayer(deps)

  } yield buck

  rt.unsafeRun(prog <* putStrLn("Done !!!"))

}
