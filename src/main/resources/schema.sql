CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    retrieval_interval INT,
    is_vip BOOLEAN
);

CREATE TABLE IF NOT EXISTS vendor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT,
    tickets_per_release INT,
    release_interval INT
);

CREATE TABLE IF NOT EXISTS ticket (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT,
    is_vip BOOLEAN
);