package dao

import java.sql.Time
import java.util.Date
import javax.inject.Inject

import models.Post
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

/**
 * Created by rumata on 11.12.15.
 */
class PostDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Posts = TableQuery[PostsTable]

  def all(): Future[Seq[Post]] = db.run(Posts.sortBy(_.createdAt).result)

  def insert(post: Post): Future[Unit] = db.run(Posts += post).map { _ => () }

  def remove(postId: Long) = db.run(Posts.filter(p => p.id === postId).delete)

  class PostsTable(tag: Tag) extends Table[Post](tag, "posts") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def content = column[String]("content")

    def createdAt = column[String]("created_at")

    def modifiedAt = column[String]("modified_at")

    def * = (id.?, title, content, createdAt.?, modifiedAt.?) <>((Post.apply _).tupled, Post.unapply _)
  }

}

