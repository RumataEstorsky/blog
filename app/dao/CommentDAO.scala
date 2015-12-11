package dao

import javax.inject.Inject

import models.Comment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

/**
 * Created by rumata on 11.12.15.
 */
class CommentDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Comments = TableQuery[CommentsTable]

  def all(): Future[Seq[Comment]] = db.run(Comments.sortBy(_.createdAt).result)

  def insert(comment: Comment): Future[Unit] = db.run(Comments += comment).map { _ => () }

  def remove(postId: Long, commentId: Long) = db.run(Comments.filter(c => c.id === commentId && c.postId == postId).delete)

  class CommentsTable(tag: Tag) extends Table[Comment](tag, "comments") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def postId = column[Long]("post_id")

    def author = column[String]("author")

    def content = column[String]("content")

    def createdAt = column[String]("created_at")

    def * = (id.?, postId, author, content, createdAt.?) <>((Comment.apply _).tupled, Comment.unapply _)
  }
}
