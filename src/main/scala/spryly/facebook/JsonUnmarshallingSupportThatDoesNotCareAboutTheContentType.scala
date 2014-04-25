/*
 * Copyright © 2013-2014 Spryly <http://spryly.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spryly.facebook

import spray.http._
import spray.httpx.unmarshalling._
import spray.json._
import DefaultJsonProtocol._

/** This is here because there's some silly bug in the Sublime scala theme
  * that causes everything after the MediaRanges usage to appear as a comment.
  * I wanted to minimize the impact on how crappy it makes everything look
  * @author Andy Scott
  */
object JsonUnmarshallingSupportThatDoesNotCareAboutTheContentType {
  implicit def unmarshallerThatDoesntCareConverter[T](reader: RootJsonReader[T]) =
    unmarshallerThatDoesntCare(reader)
  implicit def unmarshallerThatDoesntCare[T: RootJsonReader]: Unmarshaller[T] =
    Unmarshaller[T](MediaRanges.`*/*`) {
      case x: HttpEntity.NonEmpty ⇒
        val json = JsonParser(x.asString(defaultCharset = HttpCharsets.`UTF-8`))
        jsonReader[T].read(json)
    }
}