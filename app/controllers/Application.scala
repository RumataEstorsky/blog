package controllers

import javax.inject.Inject

import dao.PostDAO
import models.Post
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import scala.concurrent.Future

class Application @Inject()(postDao: PostDAO) extends Controller {

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

  def removePost(postId: Long) = TODO

  def createComment(postId: Long) = TODO

  def removeComment(postId: Long, commentId: Long) = TODO


}
