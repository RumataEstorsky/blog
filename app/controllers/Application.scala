package controllers

import javax.inject.Inject

import dao.{CommentDAO, PostDAO}
import models.Post
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class Application @Inject()(postDao: PostDAO, commentDAO: CommentDAO) extends Controller {

  def index = Action.async {
    postDao.all().map { posts => Ok(toJson(posts)) }
  }

  def getComments(postId: Long) = TODO

  def createPost = Action.async(parse.json) { implicit request =>
    request.body.validate[Post].map { post =>
      postDao.insert(post).map(_ => Ok(toJson(post)))
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def updatePost(postId: Long) = TODO

  def removePost(postId: Long) = Action {
    postDao.remove(postId)
    Ok("") //TODO is it really need?
  }

  def createComment(postId: Long) = TODO

  // TODO argument postId is excess!
  def removeComment(postId: Long, commentId: Long) = Action {
    commentDAO.remove(postId, commentId)
    Ok("") //TODO is it really need?
  }


}
