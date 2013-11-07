package models

import org.squeryl.KeyedEntity
import org.squeryl.dsl.{StatefulOneToMany, OneToMany}

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 15:30
 *
 */
case class Warehouse (id: Long, name: String)    extends KeyedEntity[Long]  {

  lazy val stockItems:OneToMany[StockItem] = Database.warehouseToStockItems.left(this)

  /**STATEFUL RELATIONSHIPS (ORM Like) */
  // because a stateful relations gets the list of related entities during initialization
  // it should always be lazy otherwise it's not possible to instantiate an entity outside a transaction
  // you need to be in a transaction also the first time you access the list of related entities
  lazy val stockItemsStateful:StatefulOneToMany[StockItem] = Database.warehouseToStockItems.leftStateful(this)
}


object Warehouse{
  def find() = {
    List("W123", "W456")
  }
}