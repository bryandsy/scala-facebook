/*                         _                                       *\
**    ___ ____   ____    _| |_   _                                 **
**   /___)  _ \ / ___)| | | | | | |   Spryly                       **
**  |___ | | | | |  | |_| | | |_| |   (c) 2014, Spryly             **
**  (___/| ||_/|_|   \__  |_|\__  |   http://spryly.com            **
**       |_|        (____/  (____/                                 **
\*                                                                 */

package spryly.facebook

import akka.actor._
import akka.io.IO

import spray.can.Http
import spray.http._
import spray.http.HttpMethods._
import spray.httpx.unmarshalling._

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import scala.util.Try

/** Facebook API actor companion
  * @author Andy Scott
  */
object Facebook {
  def props() = Props[Facebook]
}

/** Facebook API actor
  * @author Andy Scott
  */
class Facebook extends Actor {
  import Endpoints._
  import Facebook._
  import GraphUnmarshallers._

  val http = IO(Http)(context.system)

  /** Computes a hmac in the same manner as hash_hmac in PHP
    */
  private def hmac(data: String, key: String): String = {
    val secretKey = new SecretKeySpec(key getBytes "UTF-8", "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac init secretKey
    mac
      .doFinal(data.getBytes("UTF-8"))
      .map("%02x" format _).mkString
  }

  def receive = {
    case me: GraphMessages#Me ⇒

      http ! HttpRequest(method = GET, uri = ME.withQuery(
        Map("access_token" -> me.token) ++ me.secret
          .map { secret ⇒ hmac(me.token, secret) }
          .map("appsecret_proof" -> _)
      ))

    case resp: HttpResponse ⇒

      Console println resp.entity.as[User]

  }

}

/** Facebook endpoint URIs
  */
object Endpoints {
  val graphBase = "https://graph.facebook.com/"
  val ME = Uri(s"${graphBase}me")
}

/** Very simple test app, to be turned into tests... later.
  */
object FacebookApp extends App {

  val session = GraphSession(
    sys.env("FB_TOKEN"),
    Try(sys.env("FB_APPSECRET")).toOption)

  val system = ActorSystem()

  val actor = system.actorOf(Facebook.props)

  actor ! session.Me()

  Thread sleep 5000

  system.shutdown()
}