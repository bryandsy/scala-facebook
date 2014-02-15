/*                         _                                       *\
**    ___ ____   ____    _| |_   _                                 **
**   /___)  _ \ / ___)| | | | | | |   Spryly                       **
**  |___ | | | | |  | |_| | | |_| |   (c) 2014, Spryly             **
**  (___/| ||_/|_|   \__  |_|\__  |   http://spryly.com            **
**       |_|        (____/  (____/                                 **
\*                                                                 */

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