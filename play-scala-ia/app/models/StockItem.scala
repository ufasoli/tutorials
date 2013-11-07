package models

import org.squeryl.KeyedEntity
import org.squeryl.dsl.{StatefulManyToOne, StatefulOneToMany, ManyToOne}
import models.Warehouse

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 15:30
 *
 */
case class StockItem(
                      id: Long,
                      productId: Long,
                      warehouseId: Long,
                      quantity: Long
                      ) extends KeyedEntity[Long]  {

    // assign right side of relationship to this
  lazy val product:ManyToOne[Product] = Database.productToStockItems.right(this)
  lazy val warehouse:ManyToOne[Warehouse] = Database.warehouseToStockItems.right(this)


  /**STATEFUL RELATIONSHIPS (ORM Like) */
  // because a stateful relations gets the list of related entities during initialization
  // it should always be lazy otherwise it's not possible to instantiate an entity outside a transaction
  // you need to be in a transaction also the first time you access the list of related entities
  lazy val productStateful:StatefulManyToOne[Product] = Database.productToStockItems.rightStateful(this)
  lazy val warehouseStateful:StatefulManyToOne[Warehouse] = Database.warehouseToStockItems.rightStateful(this)
}

object StockItem{
  import org.squeryl.PrimitiveTypeMode._

  def getLargeStockQ(product:Product, quantity:Long)=
    from(product.stockItems) (s =>
      where(s.quantity gt quantity)
        select(s)
    )

  def getLargeStock(product:Product, quantity:Long):Iterable[Long]={
    from(getLargeStockQ(product,quantity)){stockItem =>
      select(stockItem.quantity)
    }
  }

  def getStockItemsSqueryl(product:Product)={
    product.stockItems.toList
  }
}
