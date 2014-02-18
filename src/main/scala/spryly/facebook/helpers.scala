/*                         _                                       *\
**    ___ ____   ____    _| |_   _                                 **
**   /___)  _ \ / ___)| | | | | | |   Spryly                       **
**  |___ | | | | |  | |_| | | |_| |   (c) 2014, Spryly             **
**  (___/| ||_/|_|   \__  |_|\__  |   http://spryly.com            **
**       |_|        (____/  (____/                                 **
\*                                                                 */

package spryly.facebook

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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