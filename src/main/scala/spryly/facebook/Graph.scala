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

case class Page(
  id: String,
  name: Option[String])

private[facebook] sealed trait PageJson {
  implicit val pageFormat = jsonFormat2(Page)
}

trait PageUnmarshaller extends PageJson {
  implicit val pageUnmarshaller = implicitly[Unmarshaller[Page]]
}

object User {

  case class AgeRange(
    /** enum 13, 18, 21 */
    min: String,
    /** enum 17, 20, none */
    max: String)

  case class Currency(
    user_currency: String,
    usd_exchange: Float,
    usd_exchange_inverse: Float)

}

case class User(
  id: String,
  age_range: Option[User.AgeRange],
  bio: Option[String],
  /** format  MM/DD/YYYY */
  birthday: Option[String],
  currency: Option[User.Currency],
  name: Option[String],
  email: Option[String],
  favorite_athletes: Option[List[Page]],
  favorite_teams: Option[List[Page]],
  first_name: Option[String],
  gender: Option[String],
  hometown: Option[Page],
  inspirational_people: List[Page],
  installed: Option[Boolean],
  is_verified: Option[Boolean],
  languages: List[Page],
  last_name: Option[String],
  link: Option[String],
  locale: Option[String],
  location: Option[Page])

private[facebook] sealed trait UserJson extends PageJson {
  implicit val ageRageFormat = jsonFormat2(User.AgeRange)
  implicit val currenctFormat = jsonFormat3(User.Currency)
  implicit val userFormat = jsonFormat20(User.apply)
}

trait UserUnmarshaller extends UserJson {
  implicit val userUnmarshaller = implicitly[Unmarshaller[User]]
}

object GraphUnmarshallers extends UserUnmarshaller