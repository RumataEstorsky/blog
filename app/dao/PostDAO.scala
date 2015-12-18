package dao

import javax.inject.Inject

import models.Post
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future
import com.github.tototoshi.slick.PostgresJodaSupport._


/**
 * Created by rumata on 11.12.15.
 */
class PostDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Posts = TableQuery[PostsTable]

  def page(pageIndex: Int, pageSize: Int = 10): Future[Seq[Post]] =
    db.run(Posts.sortBy(_.createdAt).drop(pageIndex * pageSize).take(pageSize).result)

  def findById(postId: Long) = db.run(Posts.filter(p => p.id === postId).result.headOption)

  def insert(post: Post): Future[Unit] = db.run(Posts += post).map { _ => () }

  def update(post: Post): Future[Unit] = db.run(Posts.filter(p => p.id === post.id).update(post)).map { _ => () }


  def remove(postId: Long) = db.run(Posts.filter(p => p.id === postId).delete).map(_ => ())


  class PostsTable(tag: Tag) extends Table[Post](tag, "posts") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def content = column[String]("content")

    def createdAt = column[DateTime]("created_at")

    def modifiedAt = column[DateTime]("modified_at")

    def commentsCount = column[Int]("comments_count")

    def * = (id.?, title, content, createdAt, modifiedAt.?, commentsCount) <>((Post.apply _).tupled, Post.unapply _)
  }

}

