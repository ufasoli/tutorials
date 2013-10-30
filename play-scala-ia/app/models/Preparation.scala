package models

case class Preparation(orderNumber: Long, product: Product,
                       quantity: Int, location: String)
