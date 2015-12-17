package models

import org.joda.time.DateTime
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
 * Created by rumata on 11.12.15.
 */
case class Post(id: Option[Long] = None,
                title: String,
                content: String,
                createdAt: DateTime,
                modifiedAt: Option[DateTime] = None,
                commentsCount: Int = 0
                 )

object Post {
  import Blog.jodaDateWrites

  implicit val postReads: Reads[Post] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "title").read[String](maxLength[String](256)) and
    (JsPath \ "content").read[String](maxLength[String](2048)) and
    ((JsPath \ "createdAt").read[DateTime] or Reads.pure(new DateTime())) and
    (JsPath \ "modifiedAt").readNullable[DateTime] and
    ((JsPath \ "commentsCount").read[Int] or Reads.pure(0))
  )(Post.apply _)

  implicit val postWrites: Writes[Post] = (
    (JsPath \ "id").writeNullable[Long] and
    (JsPath \ "title").write[String] and
    (JsPath \ "content").write[String] and
    (JsPath \ "createdAt").write[DateTime] and
    (JsPath \ "modifiedAt").writeNullable[DateTime] and
    (JsPath \ "commentsCount").write[Int]
  )(unlift(Post.unapply))

  implicit val postFormat: Format[Post] = Format(postReads, postWrites)
  //    implicit val postFormat: Format[Post] = Json.format[Post]
}
