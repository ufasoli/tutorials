package models

import play.api.db.DB
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.dsl.{StatefulOneToMany, OneToMany}
import play.api.cache.Cache

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 10:17
 *
 */

case class Product(id: Long,
                   ean: Long,
                   name: String,
                   description: String) extends KeyedEntity[Long]    {

  /** STATELESS RELATIONSHIPS**/
  // assign left side of relationship to itself
  lazy val stockItems: OneToMany[StockItem] = Database.productToStockItems.left(this)

  /**STATEFUL RELATIONSHIPS (ORM Like) */
  // because a stateful relations gets the list of related entities during initialization
  // it should always be lazy otherwise it's not possible to instantiate an entity outside a transaction
  // you need to be in a transaction also the first time you access the list of related entities
  lazy val stockItemsStateful: StatefulOneToMany[StockItem] = Database.productToStockItems.leftStateful(this)
}

object Product {

  // anorm imports

  import anorm.SQL
  import anorm.SqlQuery

  // Squeryl Imports

  import Database.{productsTable, stockItemsTable}
  import org.squeryl.PrimitiveTypeMode._
  import org.squeryl.Table
  import org.squeryl.Query
  import collection.Iterable
  import play.api.Play.current

  var products = Set(
    Product(1L, 5010255079763L, "Paperclips Large", "Large Plain Pack of 1000"),
    Product(2L, 5018206244666L, "Giant Paperclips",  "Giant Plain 51mm 100 pack"),
    Product(3L, 5018306332812L, "Paperclip Giant Plain",  "Giant Plain Pack of 10000"),
    Product(4L, 5018306312913L, "No Tear Paper Clip", "No Tear Extra Large Pack of 1000"),
    Product(5L, 5018206244611L, "Zebra Paperclips","Zebra Length 28mm Assorted 150 Pack")
  )

  def findAll = products.toList.sortBy(_.ean)

  def findByEan(ean: Long) = products.find(_.ean == ean)

  def add(product: Product) = products = products + product

  def delete(ean: Long) = products = products.filter(_.ean != ean)


  val sql: SqlQuery = SQL("select * from products order by name asc")

  def findAllDBStream: List[Product] = DB.withConnection {
    //create connection before running code and close afterwards
    implicit connection => // make connection available
      sql().map(row => //iterate over rows
        Product(row[Long]("id"), row[Long]("ean"), row[String]("name"), row[String]("description")) //initialize object from row cols
      ).toList // streams are lazy so force fetch the elemens by converting to list
  }

  def findAllDBPatternMatching: List[Product] = DB.withConnection {
    implicit connection =>
      import anorm.Row
      sql().collect {
        // collect is a partial function
        // for each row that matches the case create a new product
        // anorm wraps each value from a column inside a Some
        case Row(Some(id: Long), Some(ean: Long), Some(name: String), Some(description: String)) => Product(id, ean, name, description)
      }.toList

  }


  def findAllDBCombinators: List[Product] = DB.withConnection {
    implicit connection =>
      import Parsers._
      sql.as(productsParser)
  }

  def findAllProductsWithStockItems: Map[Product, List[StockItem]] = {
    import Parsers._
    DB.withConnection {
      implicit connection =>

      // join query
        val sql: SqlQuery = SQL("SELECT p.*, s.* FROM products p INNER JOIN stock_items on (p.id = s.product_id")

        // use parser to parse results
        val results: List[(Product, StockItem)] = sql.as(productStockItemParser *)
        // group by results and convert a list of tupes into a Map with associated list
        results.groupBy {
          _._1
        }.mapValues(_.map {
          _._2
        })

      // calling group by on the products converts the result into a Map[Product, (Product,StockItem)]
      //which is why we map over the values and for each value we map over each list to produce a Map[Prodct, List[StockItem]]

    }

  }

  //**********************************
  //Anorm Methods
  //**********************************

  def insertDB(product: Product): Int = DB.withConnection {
    implicit connection =>

      SQL(
        """INSERT INTO products
          | VALUES ({id}, {ean}, {name}, {description})""").on(
        "id" -> product.id,
        "ean" -> product.ean,
        "name" -> product.name,
        "description" -> product.description
      ).executeUpdate() // will return number of affected rows (1 un our case)


  }

  def updateDB(product: Product): Int = DB.withConnection {
    implicit connection =>

      SQL(
        """UPDATE products SET name={name}, ean={ean}, description={description} WHERE id={id}""")
        .on(
        "id" -> product.id,
        "ean" -> product.ean,
        "name" -> product.name,
        "description" -> product.description
      )
        .executeUpdate()
  }

  def deleteDB(product: Product): Int = DB.withConnection {
    implicit connection =>

      SQL("DELETE FROM Products WHERE id={id}").on("id" -> product.id).executeUpdate()
  }

  //**********************************
  //Squeryl Methods
  //**********************************


  def allQ: Query[Product] = from(productsTable) {
    // from where to SELECT
    product =>
      select(product).orderBy(product.name desc)

  }

  def findAllSqueryl: Iterable[Product] = inTransaction {
    allQ.toList
  }

  def productsInWarehouseSqueryl(warehouse: Warehouse): Query[Product] = {
    join(productsTable, stockItemsTable)((product, stockItem) => // join 2 tables
      where(stockItem.warehouseId === warehouse.id).select(product)
        .on(stockItem.productId === product.id) // what should the join be made on
    )
  }

  def productsInWarehouseByNaneSqueryl(name: String, warehouse: Warehouse): Query[Product] = {
    // passing a query to the from function instead of a table
    from(productsInWarehouseSqueryl(warehouse)) {
      product =>
        where(product.name like name).select(product)
    }

  }

  def insertSqueryl(product:Product):Product = inTransaction{

    val defensiveCopy = product.copy()


    val insertedProduct = productsTable.insert(defensiveCopy)

    Cache.set("product-"+product.id, insertedProduct)
    insertedProduct
  }

  def updateSqueryl(product:Product){
    inTransaction{ productsTable.update(product) }
  }

  def getProductFromCache(id:Long):Product ={
    Cache.getAs[Product]("product-"id) match{
      case  Some(product:Product)  => product
      case _ =>

    }
  }





}
