package models

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
 * Created by rumata on 11.12.15.
 */
case class Comment(id: Option[Long] = None,
                   postId: Long,
                   author: String,
                   content: String,
                   createdAt: DateTime)

object Comment {

  implicit val commentReads: Reads[Comment] = (
    (JsPath \ "id").readNullable[Long] and
    ((JsPath \ "postId").read[Long]  or Reads.pure(-1L)) and //TODO -1
    (JsPath \ "author").read[String](maxLength[String](128)) and
    (JsPath \ "content").read[String](maxLength[String](500)) and
    ((JsPath \ "createdAt").read[DateTime] or Reads.pure(new DateTime()))
  )(Comment.apply _)

  implicit val commentWrites: Writes[Comment] = (
    (JsPath \ "id").writeNullable[Long] and
    (JsPath \ "postId").write[Long] and
    (JsPath \ "author").write[String] and
    (JsPath \ "content").write[String] and
    (JsPath \ "createdAt").write[DateTime]
  )(unlift(Comment.unapply))

  implicit val commentFormat: Format[Comment] = Format(commentReads, commentWrites)


//  implicit val commentFormat = Json.format[Comment]
}

