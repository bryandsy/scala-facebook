/*                         _                                       *\
**    ___ ____   ____    _| |_   _                                 **
**   /___)  _ \ / ___)| | | | | | |   Spryly                       **
**  |___ | | | | |  | |_| | | |_| |   (c) 2014, Spryly             **
**  (___/| ||_/|_|   \__  |_|\__  |   http://spryly.com            **
**       |_|        (____/  (____/                                 **
\*                                                                 */

package spryly.facebook

import spray.json._
import DefaultJsonProtocol._

import spray.http._
import spray.httpx.unmarshalling._

import JsonUnmarshallingSupportThatDoesNotCareAboutTheContentType._

trait GraphMessages {

  val token: String
  val secret: Option[String]

  sealed trait Operation {
    def token = GraphMessages.this.token
    def secret = GraphMessages.this.secret
  }

  case class Me() extends Operation
}

case class GraphSession(
  token: String, secret: Option[String] = None) extends GraphMessages

case class User(
  id: String,
  bio: Option[String],
  name: Option[String],
  email: Option[String])

trait UserUnmarshaller {
  implicit val userFormat = jsonFormat4(User)
  implicit val userUnmarshaller = implicitly[Unmarshaller[User]]
}

object GraphUnmarshallers extends UserUnmarshaller