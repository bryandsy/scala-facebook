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
import spray.httpx.unmarshalling._

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
  import Facebook._
  import GraphUnmarshallers._

  val http = IO(Http)(context.system)

  def receive = {
    case me: GraphMessages#Me ⇒

      http ! me.toRequest

    case resp: HttpResponse ⇒

      import sext._

      Console println resp.entity.as[User].valueTreeString

  }

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