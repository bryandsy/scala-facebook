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
import spray.http.HttpMethods._
import spray.httpx.unmarshalling._

import JsonUnmarshallingSupportThatDoesNotCareAboutTheContentType._

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/** Facebook endpoint URIs
  */
object Endpoints {
  val graphBase = "https://graph.facebook.com/"
  val Me = Uri(s"${graphBase}me")
}

object HMAC {
  /** Computes a hmac in the same manner as hash_hmac in PHP
    */
  def compute(data: String, key: String): String = {
    val secretKey = new SecretKeySpec(key getBytes "UTF-8", "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac init secretKey
    mac
      .doFinal(data.getBytes("UTF-8"))
      .map("%02x" format _).mkString
  }
}

trait GraphMessages {

  val token: String
  val secret: Option[String]

  lazy val cred = Map("access_token" -> token) ++ proof
  lazy val proof = secret
    .map { secret ⇒ HMAC.compute(token, secret) }
    .map("appsecret_proof" -> _)

  sealed trait Operation {
    def toRequest = HttpRequest(method = GET, uri = Endpoints.Me.withQuery(
      cred
    ))
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