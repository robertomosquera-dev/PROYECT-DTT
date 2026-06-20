-- ============================================================
-- SCRIPT 1: CREACIÓN DE TABLAS
-- Proyecto: ms-catalog
-- ============================================================

-- -----------------------------------------------
-- 1. CATEGORIES (sin dependencias)
-- -----------------------------------------------
CREATE TABLE categories
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(100) NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ENABLED',
    parent_id  UUID,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    path       VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
);

-- -----------------------------------------------
-- 2. PRODUCTS (sin dependencias)
-- -----------------------------------------------
CREATE TABLE products
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ENABLED',
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------
-- 3. SALE_PRODUCTS (depende de products)
-- -----------------------------------------------
CREATE TABLE sale_products
(
    id         UUID PRIMARY KEY,
    product_id UUID           NOT NULL UNIQUE,
    price      NUMERIC(10, 2) NOT NULL,
    stock      INTEGER        NOT NULL,
    status     VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    deleted    BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- -----------------------------------------------
-- 4. RENTAL_PRODUCTS (depende de products)
-- -----------------------------------------------
CREATE TABLE rental_products
(
    id               UUID PRIMARY KEY,
    product_id       UUID           NOT NULL UNIQUE,
    weekly_price     NUMERIC(10, 2) NOT NULL,
    monthly_price    NUMERIC(10, 2) NOT NULL,
    security_deposit NUMERIC(10, 2) NOT NULL,
    stock            INTEGER        NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    deleted          BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_rental_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- -----------------------------------------------
-- 5. PRODUCT_BUNDLES (sin dependencias directas en la tabla)
-- -----------------------------------------------
CREATE TABLE product_bundles
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(200)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    deleted     BOOLEAN        NOT NULL DEFAULT FALSE,
    stock       INTEGER        NOT NULL,
    rolls_count INTEGER        NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------
-- 6. BUNDLE_ITEMS (depende de product_bundles y sale_products)
-- -----------------------------------------------
CREATE TABLE bundle_items
(
    id              UUID PRIMARY KEY,
    bundle_id       UUID      NOT NULL,
    sale_product_id UUID      NOT NULL,
    weight          INTEGER   NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_bundle_item_bundle FOREIGN KEY (bundle_id) REFERENCES product_bundles (id),
    CONSTRAINT fk_bundle_item_sale_product FOREIGN KEY (sale_product_id) REFERENCES sale_products (id)
);

-- -----------------------------------------------
-- 7. PRODUCT_CATEGORIES (tabla join: products <-> categories)
-- -----------------------------------------------
CREATE TABLE product_categories
(
    product_id  UUID NOT NULL,
    category_id UUID NOT NULL,

    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- -----------------------------------------------
-- 8. BUNDLE_CATEGORIES (tabla join: product_bundles <-> categories)
-- -----------------------------------------------
CREATE TABLE bundle_categories
(
    bundle_id   UUID NOT NULL,
    category_id UUID NOT NULL,

    PRIMARY KEY (bundle_id, category_id),
    CONSTRAINT fk_bc_bundle FOREIGN KEY (bundle_id) REFERENCES product_bundles (id),
    CONSTRAINT fk_bc_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- -----------------------------------------------
-- 9. IMAGES (polimórfica, sin FK directa)
-- -----------------------------------------------
CREATE TABLE images
(
    id         UUID PRIMARY KEY,
    owner_id   UUID        NOT NULL,
    owner_type VARCHAR(20) NOT NULL, -- 'PRODUCT' | 'BUNDLE'
    url        TEXT        NOT NULL,
    img_order  SMALLINT,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------
-- 10. RESERVATIONS_STOCK
-- -----------------------------------------------
CREATE TABLE reservations_stock
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    order_id   UUID        NOT NULL,
    estado     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------
-- 11. RESERVATION_ITEM_STOCK (depende de reservations_stock)
-- -----------------------------------------------
CREATE TABLE reservation_item_stock
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID        NOT NULL,
    product_id     UUID        NOT NULL,
    type           VARCHAR(20) NOT NULL, -- ProductType enum
    quantity       INTEGER     NOT NULL,

    CONSTRAINT fk_ris_reservation FOREIGN KEY (reservation_id) REFERENCES reservations_stock (id)
);