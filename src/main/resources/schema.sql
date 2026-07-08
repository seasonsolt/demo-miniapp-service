CREATE TABLE IF NOT EXISTS e2e_probe (
    probe_id VARCHAR(128) PRIMARY KEY,
    probe_value VARCHAR(1024) NOT NULL,
    updated_by VARCHAR(128) NOT NULL,
    updated_at VARCHAR(64) NOT NULL
);
