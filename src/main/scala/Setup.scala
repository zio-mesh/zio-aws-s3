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

import zio.crew.s3.compat.JavaConverters._

object setup {
  val env              = System.getenv.asScala
  val bucket           = env("AWS_BUCKET")
  val endpointOverride = env.get("AWS_ENDPOINT_OVERRIDE")
  val region           = software.amazon.awssdk.regions.Region.US_WEST_2
}
