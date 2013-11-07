package controllers

import play.api.mvc.{Flash, Controller, Action}
import models.{Database, StockItem, Product}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import play.api.libs.json.Json
import org.squeryl.PrimitiveTypeMode.transaction
import Database.{stockItemsTable, productsTable}

/**
 * Created with IntelliJ IDEA.
 * User: Ulises Fasoli
 * Date: 30.10.13
 * Time: 10:10
 *
 */
object Products extends Controller {

  private val productForm: Form[Product] = Form(
    mapping(
      "id" -> longNumber,
      "ean" -> longNumber.verifying("validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )



  def list = Action {
    implicit request =>
      val products = Product.findAll

      Ok(views.html.products.list(products))
  }

  def show(ean: Long) = Action {
    implicit request =>

      Product.findByEan(ean).map {
        product =>
          Ok(views.html.products.detail(product))

      }.getOrElse(NotFound) // if no product was found the map above will return none and so the Else (NotFound) will be invoked
  }

  def save = Action { implicit request =>

    val newProductForm = productForm.bindFromRequest()
      newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.Products.newProduct()).
          flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
    success = { newProduct =>
        Product.add(newProduct)
        val message = Messages("products.new.success", newProduct.name)
        Redirect(routes.Products.show(newProduct.ean)).flashing("success" ->message)
      }
    )

  }

  def newProduct = Action { implicit request =>
    val form = if(flash.get("error").isDefined)
      productForm.bind(flash.data)
    else
      productForm

    Ok(views.html.products.editProduct(form))
  }

  def statuses(product: Product) = Action{
    val url = routes.Products.show(product.ean).url


    Created.withHeaders(LOCATION -> url)  // returns CREATED HTTP Status with location header
    Status(501) // returns HTTP 501 status

    // returning JSON in 2 ways
    val jSON = """{"status" : "success"}"""
    Ok(jSON).withHeaders(CONTENT_TYPE -> JSON)
    Ok(jSON).as(JSON)
    Ok(Json.toJson(Map("status" -> "success")))
  }


  def sessionData = Action{ implicit  request =>

    // adding data to the session
    Ok("""{"status" : "success"}""").withSession(request.session + ("search.previous" -> "myQuery"))
    // will make request.session.get("search.previous") available

    // removing data from the session
    Ok("""{"status" : "success"}""").withSession(request.session - "search.previous")


  }

  def details(ean:Long) = Action{
    Ok(s"""{"ean" : $ean}""")
  }

  def alias(alias:String)= Action{
    Ok(s"""{"ean" : $alias}""")
  }

  def delete(ean:Long)=Action{
    Product.delete(ean)
    Redirect(routes.Products.list())
  }

  def edit(ean:Long)= Action{
    NotImplemented
  }

  def update(ean:Long)= Action{
    NotImplemented
  }


  def addNewProductGood(product:Product, stockItem:StockItem){
    // good operations wrapped in a transaction
    // if stockItem fails it will rollback both operations
    transaction{
        productsTable.insert(product)
      stockItemsTable.insert(stockItem)
    }
  }

  def addNewProductBad(product:Product, stockItem:StockItem){

    // bad approach since not wrapped into a transaction
    // so if stockItem fails we will have a product but no stock item

    // each method will create it's own transaction
    productsTable.insert(product)
    stockItemsTable.insert(stockItem)
  }


  def addNewStokItem(product:Product, stockItem:StockItem){

    product.stockItems.assign(stockItem)

    transaction{
      Database.stockItemsTable.insert(stockItem)
    }
  }


}
