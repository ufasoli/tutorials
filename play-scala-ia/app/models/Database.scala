package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Table, Schema}

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 07.11.13
 * Time: 12:47
 *
 */
object Database extends Schema {

  val productsTable: Table[Product] = table[Product]("products")
  val warehousesTable: Table[Warehouse] = table[Warehouse]("warehouses")
  val stockItemsTable: Table[StockItem] = table[StockItem]("stockitems")

  val productToStockItems = oneToManyRelation(productsTable, stockItemsTable).
    via((p,s) => p.id === s.productId)

  val warehouseToStockItems = oneToManyRelation(warehousesTable,stockItemsTable).
    via((w,s) => w.id === s.warehouseId)

  on(this.productsTable) {
    p =>
      declare(
        p.id is (autoIncremented),
        p.ean is (unique)
      )
  }

  on(this.warehousesTable) {
    w =>
      declare(
        w.id is (autoIncremented),
        w.name is (unique)
      )
  }

  on(this.stockItemsTable) {
    s =>
      declare(
        s.id is (autoIncremented)
      //  columns(s.product, s.location) are (unique)
      )
  }
}

