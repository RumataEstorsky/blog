package filters

import play.api.mvc._
import sun.misc.BASE64Decoder

import scala.concurrent.Future


class BasicAuthFilter extends Filter  {
  private val realm = "You must log in to Blog service"
  private lazy val unauthResult = Results.Unauthorized.withHeaders(("WWW-Authenticate", s"""Basic realm="$realm""""))
  private lazy val username = "rumata"
  private lazy val password = "rumata"
  private lazy val basicSt = "basic "
  val enableMethods = Set("getPosts", "getComments",  "getComments",  "createComment")

  private def decodeBasicAuth(auth: String): Option[(String, String)] = {
    if (auth.length() < basicSt.length()) {
      return None
    }
    val basicReqSt = auth.substring(0, basicSt.length())
    if (basicReqSt.toLowerCase() != basicSt) {
      return None
    }
    val basicAuthSt = auth.replaceFirst(basicReqSt, "")
    //BESE64Decoder is not thread safe, don't make it a field of this object
    val decoder = new BASE64Decoder()
    val decodedAuthSt = new String(decoder.decodeBuffer(basicAuthSt), "UTF-8")
    val usernamePassword = decodedAuthSt.split(":")
    if (usernamePassword.length >= 2) {
      //account for ":" in passwords
      return Some(usernamePassword(0), usernamePassword.splitAt(1)._2.mkString)
    }
    None
  }


  def apply(nextFilter: (RequestHeader) => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {
    val MN = "ROUTE_ACTION_METHOD"
    if (requestHeader.tags.contains(MN) && enableMethods.contains(MN)) {
      return nextFilter(requestHeader)
    } else {
      requestHeader.headers.get("authorization").map { basicAuth =>
        decodeBasicAuth(basicAuth) match {
          case Some((user, pass)) => {
            if (username == user && password == pass) {
              return nextFilter(requestHeader)
            }
          }
          case _ => ;
        }
        return Future.successful(unauthResult)
      }.getOrElse({
        Future.successful(unauthResult)
      })
    }
  }

}