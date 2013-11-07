package models

object PickList {

  def find(warehouse: String) : List[Preparation] = {
    val p = Product(1L, 5010255079763L, "Large paper clips 1000 pack", null)
    List(
      Preparation(3141592, p, 200, "Aisle 42 bin 7"),
      Preparation(6535897, p, 500, "Aisle 42 bin 7"),
      Preparation(93, p, 100, "Aisle 42 bin 7")
    )
  }
}