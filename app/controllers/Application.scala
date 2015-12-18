package controllers

import javax.inject.Inject

import dao.{CommentDAO, PostDAO}
import models.{Blog, Comment, Post}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import org.joda.time.DateTime
import scala.concurrent.Future

class Application @Inject()(postDao: PostDAO, commentDao: CommentDAO) extends Controller {
  val Success = Ok(Json.obj("result" -> "Success")) // TODO Special classes
  val InvalidJsonFuture = Future.successful(BadRequest(Json.obj("result" -> "Invalid JSON")))

  // TODO JSON Answer
  private def PostNotFoundFuture(postId: Long) = Future.successful(NotFound("Not found post with ID = " + postId))

  def getPosts(page: Int) = Action.async {
    postDao.page(page - 1).map { posts => Ok(Blog.decoratePostList(posts)) }
  }


  def createPost = Action.async(parse.json) { implicit request =>
    request.body.validate[Post].map { post =>
      postDao.insert(post).map(_ => Created(toJson(post)))
    }.getOrElse(InvalidJsonFuture)
  }

  def updatePost(postId: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Post].map { inputPost =>
      postDao.findById(postId).flatMap { maybePost =>
        maybePost.fold { PostNotFoundFuture(postId) } { dbPost =>
          // one or second or both
          val postToUpdate = dbPost.copy(
            title = inputPost.title,
            content = inputPost.content,
            modifiedAt = Some(new DateTime())
          )
          postDao.update(postToUpdate).map(_ => Accepted(toJson(postToUpdate)))
        }
      }
    }.getOrElse(InvalidJsonFuture)
  }

  def removePost(postId: Long) = Action.async {
    for{ _ <- postDao.remove(postId) } yield Success
  }

  def getComments(postId: Long, page: Int) = Action.async {
    postDao.findById(postId).flatMap { maybePost =>
      maybePost.fold { PostNotFoundFuture(postId) } { post =>
        commentDao.pageForPost(postId, page - 1).map{ comments => Ok(Blog.decorateCommentList(post, comments))}
      }
    }
  }

  def createComment(postId: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Comment].map { comment =>
      postDao.findById(postId).flatMap { maybePost =>
        maybePost.fold { PostNotFoundFuture(postId) } { post =>
          commentDao.insert(postId, comment).map(_ => Created(toJson(comment)))
        }
      }
    }.getOrElse(InvalidJsonFuture)
  }


  // TODO argument postId is excess!
  def removeComment(postId: Long, commentId: Long) = Action.async {
    for{ _ <- commentDao.remove(postId, commentId) } yield Success
  }


}
