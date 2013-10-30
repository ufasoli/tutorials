package controllers

import play.api.mvc.Controller
import models.Order

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 17:51
 *
 */
object Dashboard extends Controller {


  def backlog(warehouse: String) = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val backlog = scala.concurrent.future {
      models.Order.backlog(warehouse)
    }
    Async{
      backlog.map(value => Ok(value))
    }
  }
}
