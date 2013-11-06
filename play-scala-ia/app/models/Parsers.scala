package models

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 06.11.13
 * Time: 07:59
 *
 */
object Parsers {

  import anorm.RowParser
  import anorm.ResultSetParser

  implicit val productParser: RowParser[Product] = {
    import anorm.~
    import anorm.SqlParser._

    long("id") ~
      long("ean") ~
      str("name") ~
      str("description") map {

      case id ~ ean ~ name ~ description => Product(id, ean, name, description)
    }
  }



  implicit val productsParser:ResultSetParser[List[Product]] = {
    productParser.* // combine the rowparser with the resultsetparser using *
  }



  // StockItem parsers
  val stockItemParser: RowParser[StockItem] ={
    import anorm.SqlParser._
    import anorm.~

    long("id") ~
    long("product_id") ~
    long("warehouse_id") ~
    long("quantity") map{
      case id ~ productId ~ warehouseId ~ quantity  => StockItem(id, productId, warehouseId, quantity )
    }

  }

  def productStockItemParser: RowParser[(Product,StockItem)] = {
      import anorm.SqlParser._
      import anorm.~

    // combinate parsers and convert special tuple ~[Product,StockItem] to standard
    // tuple using flatten
      productParser ~ stockItemParser map (flatten)
  }
}


