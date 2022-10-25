CREATE TABLE IF NOT EXISTS bounties
(
    uuid     VARCHAR  NOT NULL,
    items    VARCHAR,
    eco      DOUBLE  NOT NULL,
    datecreated   CURRENT_DATE,
    PRIMARY KEY (uuid)
);