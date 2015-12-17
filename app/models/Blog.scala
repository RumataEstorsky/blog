package models
import play.api.libs.json._
import play.api.libs.json.Json.toJson

/**
 * Created by rumata on 11.12.15.
 */
object Blog {
  implicit val jodaDateWrites = Writes.jodaDateWrites("yyyy-MM-dd HH:mm:ss")

  val title = "My Super-Puper Blog"
  val author = "Rumata Estorsky"


  def decoratePostList(posts: Seq[Post]) = Json.obj(
    "blogTitle" -> title,
    "blogAuthor" -> author,
    "posts" -> toJson(posts)
  )

  def decorateCommentList(post: Post, comments: Seq[Comment]) = Json.obj(
    "blogTitle" -> title,
    "blogAuthor" -> author,
    "postTitle" -> post.title,
    "comments" -> comments
  )


}
