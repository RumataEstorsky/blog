package controllers

import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

import dao.{CommentDAO, PostDAO}
import models.{Blog, Post, Comment}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import play.api.libs.json._

class Application @Inject()(postDao: PostDAO, commentDao: CommentDAO) extends Controller {
  val Success = Ok(Json.obj("result" -> "Success")) // TODO Special classes

  def getPosts(page: Int) = Action.async {
    postDao.page(page - 1).map { posts => Ok(Blog.decoratePostList(posts)) }
  }


  def createPost = Action.async(parse.json) { implicit request =>
    request.body.validate[Post].map { post =>
      postDao.insert(post).map(_ => Created(toJson(post)))
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def updatePost(postId: Long) = TODO
//  Action { implicit request =>
//    request.body.validate[Post].map { post =>
//      postDao.update(postId, post).map(_ => Ok(toJson(post)))
//    }.getOrElse(Future.successful(BadRequest("invalid json")))
//  }

  def removePost(postId: Long) = Action.async {
    for{ _ <- postDao.remove(postId) } yield Success
  }

  def getComments(postId: Long, page: Int) = Action.async {
    postDao.findById(postId).flatMap { maybePost =>
      maybePost.fold {
        // TODO JSON Answer
        Future.successful(NotFound("Not found post with ID = " + postId))
      } { post =>
        commentDao.pageForPost(postId, page - 1).map{ comments => Ok(Blog.decorateCommentList(post, comments))}
      }
    }
  }

  def createComment(postId: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Comment].map { comment =>
      commentDao.insert(postId, comment).map(_ => Created(toJson(comment)))
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }


 /* def createComment(postId: Long) = Action.async(parse.json) { implicit request =>
    for {
      comment <- request.body.validate[Comment]
      Some(post) <- postDao.findById(postId)
      result <- commentDao.insert(postId, comment)
    } yield Created(toJson(result))

//    request.body.validate[Comment].flatMap { comment =>
////       commentDao.insert(postId, comment).map(_ => Created(toJson(comment)))
//      postDao.findById(postId).map {
//        case Some(p) => commentDao.insert(postId, comment).map(_ => Created(toJson(comment)))
//        case None => Future.successful(NotFound)
//      }
//    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }*/

  // TODO argument postId is excess!
  def removeComment(postId: Long, commentId: Long) = Action.async {
    for{ _ <- commentDao.remove(postId, commentId) } yield Success
  }


}
