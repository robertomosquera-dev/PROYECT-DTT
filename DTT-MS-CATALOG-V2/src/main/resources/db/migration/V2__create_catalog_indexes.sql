CREATE INDEX idx_category_path   ON categories (path);
CREATE INDEX idx_category_parent ON categories (parent_id);

CREATE INDEX idx_category_deleted ON categories (deleted);


CREATE INDEX idx_product_deleted ON products (deleted);

CREATE INDEX idx_product_status ON products (status);


CREATE INDEX idx_sale_product_product_id ON sale_products (product_id);
CREATE INDEX idx_sale_product_deleted    ON sale_products (deleted);
CREATE INDEX idx_sale_product_status     ON sale_products (status);


CREATE INDEX idx_rental_product_product_id ON rental_products (product_id);
CREATE INDEX idx_rental_product_deleted    ON rental_products (deleted);
CREATE INDEX idx_rental_product_status     ON rental_products (status);


CREATE INDEX idx_bundle_deleted ON product_bundles (deleted);
CREATE INDEX idx_bundle_status  ON product_bundles (status);


CREATE INDEX idx_bundle_item_bundle_id       ON bundle_items (bundle_id);
CREATE INDEX idx_bundle_item_sale_product_id ON bundle_items (sale_product_id);


CREATE INDEX idx_product_categories_category_id ON product_categories (category_id);

CREATE INDEX idx_bundle_categories_category_id ON bundle_categories (category_id);


CREATE INDEX idx_images_owner_id_type_new ON images (owner_id, owner_type);

CREATE INDEX idx_images_owner_order ON images (owner_id, owner_type, img_order);


CREATE INDEX idx_reservation_stock_user_id  ON reservations_stock (user_id);
CREATE INDEX idx_reservation_stock_order_id ON reservations_stock (order_id);
CREATE INDEX idx_reservation_stock_estado   ON reservations_stock (estado);


CREATE INDEX idx_ris_reservation_id ON reservation_item_stock (reservation_id);
CREATE INDEX idx_ris_product_id     ON reservation_item_stock (product_id);
CREATE INDEX idx_ris_type           ON reservation_item_stock (type);