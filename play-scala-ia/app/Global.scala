import akka.actor.{Actor, Props}
import java.util.Date
import models.{PickList, Warehouse}
import play.api.GlobalSettings
import play.api.libs.concurrent
import play.api.libs.concurrent.Akka
import play.api.templates.Html

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 17:22
 *
 */
object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    import scala.concurrent.duration._
    import play.api.Play.current

    for (warehouse <- Warehouse.find()) {

      val actor = Akka.system.actorOf(
          Props(new PickListActor(warehouse))
        )



    }

  }

}

class PickListActor(warehouse: String) extends Actor {
  def receive = {
    case "send" => {
      val pickList = PickList.find(warehouse)

      val html = views.html.pickList(warehouse, pickList, new Date)
      send(html)
    }
    case _ => {
      play.api.Logger.warn("unsupported msg type")
    }


  }

  def send(html: Html) {

  }
}
