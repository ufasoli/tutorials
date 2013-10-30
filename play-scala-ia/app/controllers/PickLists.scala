package controllers


import models.PickList
import java.util.Date
import scala.concurrent.{ExecutionContext, future}
import play.api._
import play.api.mvc._
import play.api.templates.Html

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 15:51
 *
 */
object PickLists extends Controller{

  def preview(warehouse:String) = Action{ implicit request =>
    val pickList = PickList.find(warehouse)
    val timestamp = new java.util.Date()
    Ok(views.html.pickList(warehouse,pickList,timestamp))


  }

  def sendAsync (warehouse:String) = Action { implicit request =>

    import ExecutionContext.Implicits.global

    future{
      val pickList = PickList.find(warehouse)
      send(views.html.pickList(warehouse,pickList,new Date()))
    }

    Redirect(routes.PickLists.preview(warehouse))

  }



  /**
   * Stub for ‘sending’ the pick list as an HTML document, e.g. by e-mail.
   */
  private def send(html: Html) {
    Logger.info(html.body)
    // Send pick list…
  }

}
