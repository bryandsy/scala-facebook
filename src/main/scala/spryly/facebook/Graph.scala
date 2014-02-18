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

case class GraphRequests(token: String, secret: Option[String]) {

  lazy val cred = Map("access_token" -> token) ++ proof
  lazy val proof = secret
    .map { secret ⇒ HMAC.compute(token, secret) }
    .map("appsecret_proof" -> _)

  def me() = HttpRequest(method = GET, uri = Endpoints.Me.withQuery(
    cred
  ))
}

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