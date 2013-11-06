package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 05.11.13
 * Time: 07:03
 *
 */
object Tutorialexamples extends Controller{


   def index = Action{implicit request =>

     Ok(request.flash("msg"))
   }
  def version = Action{
    Ok("1.0")
  }

  def json = Action{
    import play.api.libs.json.Json
    val success = Map("status" -> "success")
    //serializes oject to JSON + changes the content-type of th response accordingly
    Ok(Json.toJson(success))
  }

  def json2 = Action{
    // 2 ways of doing the same thing
    Ok(""" {"status" : "success"} """).withHeaders("Content-Type" -> JSON)
    Ok(""" {"status" : "success"} """).as(JSON)

    // same as 2 previous but content type is set automatically by Play (because we use Json.toJson)
    Ok(Json.toJson(Map("status" -> "success")))
  }

  def saveSessionData = Action{ implicit request =>
    Ok(routes.Products.list().url).withSession(request.session + "previous.query" -> "myQuery")
  }

  def retrieveAndDeleteFromSession = Action{implicit request =>

  // get from the session
    val prevQuery = request.session.get("previous.query")

    // Return the response and delete the query from the session
    Redirect(routes.Products.list()).withSession(request.session - "previous.query")
  }

  def xml = Action{
    // render XML as scala literal
    Ok(<status>success</status>)
  }

  def redirect = Action{
    Status(FOUND).withHeaders("location" -> routes.Products.list().url)
  }

  def withflash = Action{implicit request =>

    Redirect(routes.Tutorialexamples.index()).flashing("msg" -> "redirected from withFlash")
  }

}
