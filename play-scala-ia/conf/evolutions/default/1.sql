# --- ! UPS
CREATE TABLE Products(
  id long,
  ean long,
  name varchar,
  description varchar
)

CREATE TABLE Warehouses(

  id long,
  name varchar,


)


CREATE TABLE stock_items (
  id long,
  product_id long,
  warehouse_id long,
  quantity long
);

# --- !Downs

DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS warehouses;
DROP TABLE IF EXISTS stock_items;
