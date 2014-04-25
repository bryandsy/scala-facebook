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