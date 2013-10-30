package controllers

import play.api.mvc.{Flash, Controller, Action}
import models.Product
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages

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

}
