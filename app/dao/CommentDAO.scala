package dao

import javax.inject.Inject

import models.Comment
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import com.github.tototoshi.slick.PostgresJodaSupport._

/**
 * Created by rumata on 11.12.15.
 */
class CommentDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Comments = TableQuery[CommentsTable]

  def pageForPost(postId: Long, pageIndex: Int, pageSize: Int = 10): Future[Seq[Comment]] = {
    val commentList = Comments
      .drop(pageIndex * pageSize).take(pageSize)
      .filter(c => c.postId === postId)
      .sortBy(_.createdAt).result
    db.run(commentList)
  }


  def insert(postId: Long, comment: Comment): Future[Unit] = {
    val commentToInsert = comment.copy(postId = postId)
    db.run(Comments += commentToInsert).map { _ => () }
  }

  def remove(postId: Long, commentId: Long) = db.run(Comments.filter(c => c.id === commentId && c.postId === postId).delete).map(_ => ())

  class CommentsTable(tag: Tag) extends Table[Comment](tag, "comments") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def postId = column[Long]("post_id")

    def author = column[String]("author")

    def content = column[String]("content")

    def createdAt = column[DateTime]("created_at")

    def * = (id.?, postId, author, content, createdAt) <>((Comment.apply _).tupled, Comment.unapply _)
  }
}
