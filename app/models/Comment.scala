package models

import java.sql.Time

import play.api.libs.json.Json

/**
 * Created by rumata on 11.12.15.
 */
case class Comment(id: Option[Long] = None,
                   postId: Long,
                   author: String,
                   content: String,
                   createdAt: Option[String])

object Comment {
  //  def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t.getTime)
  //
  //  def dateTimeToTimestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)
  //
  //  implicit val timestampFormat = new Format[Time] {
  //
  //    def writes(t: Time): JsValue = toJson(t.toString)
  //
  //    def reads(json: JsValue): JsResult[Time] = new Time(System.currentTimeMillis()) //fromJson[Time](json).map(dateTimeToTimestamp)
  //
  //  }

  implicit val storeFormat = Json.format[Comment]
}

