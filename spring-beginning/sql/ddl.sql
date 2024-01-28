DROP TABLE if EXISTS member CASCADE;

CREATE TABLE member
(
    id bigint generated BY DEFAULT AS IDENTITY,
    username VARCHAR(255),
    PRIMARY KEY (id)
);