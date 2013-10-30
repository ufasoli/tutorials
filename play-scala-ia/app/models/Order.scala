package models

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 17:50
 *
 */
object Order {
  def backlog(warehouse: String): String = {
    Thread.sleep(5000L)
    new SimpleDateFormat("mmss").format(new Date())
  }
}


