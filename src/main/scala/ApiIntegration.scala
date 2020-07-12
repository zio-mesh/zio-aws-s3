/*
 * Copyright 2020 zio.crew
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

package zio.crew.s3

import zio.{ Runtime }
import zio.console.putStrLn
import setup._

object App0 extends App {

  val rt     = Runtime.default
  val prefix = "media/uploads/images/cf3a53e4-37bd-11ea-b430-6f9a089d05d1"

  // Build a layered env
  val env = AwsApp.ExtDeps.live >>> AwsApp.AwsLink.live

  // program with Deps
  val prog = AwsApp.createBucket("my-bucket")

  // program without Env deps
  val runnable = for {
    s3  <- AwsAgent.createClient(region, endpointOverride)
    out <- prog.provideLayer(env).provide(s3)
    _   = println(out)
  } yield out

  rt.unsafeRun(runnable <* putStrLn("Done !!!"))

}
