CREATE TABLE user_profile
(
    id                  VARCHAR(255) NOT NULL,
    email               VARCHAR(255),
    password            VARCHAR(255),
    first_name          VARCHAR(255),
    last_name           VARCHAR(255),
    has_profile_picture BOOLEAN      NOT NULL,
    driver_id           VARCHAR(255),
    CONSTRAINT pk_user_profile PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id VARCHAR(255) NOT NULL,
    roles   VARCHAR(255)
);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_email UNIQUE (email);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES user_profile (id);