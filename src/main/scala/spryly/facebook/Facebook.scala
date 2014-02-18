/*                   Responding      _                                       *\
**    ___ ____   ____    _| |_   _                                 **
**   /___)  _ \ / ___)| | | | | | |   Spryly                       **
**  |___ | | | | |  | |_| | | |_| |   (c) 2014, Spryly             **
**  (___/| ||_/|_|   \__  |_|\__  |   http://spryly.com            **
**       |_|        (____/  (____/                                 **
\*                                                                 */

package spryly.facebook

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http
import spray.http._
import spray.httpx.unmarshalling._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try

/** Facebook API
  * @author Andy Scott
  */
class Facebook(requests: GraphRequests)(implicit system: ActorSystem) {
  import GraphUnmarshallers._

  val http = IO(Http)(system)

  // not very pretty, and I'm sure the types/implicits can be improved greatly... but this works for now
  private def makeRequest[R, RR: Unmarshaller](req: R)(implicit timeout: Timeout, ec: ExecutionContext) =
    for (resp ← http ? req mapTo manifest[HttpResponse])
      yield resp.entity.as[RR]

  def me()(implicit timeout: Timeout, ec: ExecutionContext): Future[Deserialized[User]] =
    makeRequest(requests.me())
}

/** Very simple test app, to be turned into tests... later.
  */
object FacebookApp extends App {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(100000)

  import system.dispatcher

  val requests = GraphRequests(
    sys.env("FB_TOKEN"),
    Try(sys.env("FB_APPSECRET")).toOption)

  val facebook = new Facebook(requests)

  for (res ← facebook.me())
    Console println "> " + res

  Thread sleep 5000

  system.shutdown()
}