package models

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 15:30
 *
 */
case class Warehouse (id: Long, name: String)


object Warehouse{
  def find() = {
    List("W123", "W456")
  }
}