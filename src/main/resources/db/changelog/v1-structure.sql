--liquibase formatted sql

--changeset almi:v1-create-schema
CREATE SCHEMA IF NOT EXISTS appdata;
--rollback DROP SCHEMA appdata CASCADE;

--changeset almi:v1-create-binyan
CREATE TABLE appdata.binyan
(
    id      BIGSERIAL   NOT NULL PRIMARY KEY,
    value   VARCHAR(32) NOT NULL,
    version INT         NOT NULL DEFAULT 0,
    CONSTRAINT binyan_value_unique UNIQUE (value)
);
--rollback DROP TABLE appdata.binyan;

--changeset almi:v1-create-gizrah
CREATE TABLE appdata.gizrah
(
    id      BIGSERIAL   NOT NULL PRIMARY KEY,
    value   VARCHAR(32) NOT NULL,
    version INT         NOT NULL DEFAULT 0,
    CONSTRAINT gizrah_value_unique UNIQUE (value)
);
--rollback DROP TABLE appdata.gizrah;

--changeset almi:v1-create-preposition
CREATE TABLE appdata.preposition
(
    id      BIGSERIAL   NOT NULL PRIMARY KEY,
    value   VARCHAR(16) NOT NULL,
    version INT         NOT NULL DEFAULT 0,
    CONSTRAINT preposition_value_unique UNIQUE (value)
);
--rollback DROP TABLE appdata.preposition;

--changeset almi:v1-create-root
CREATE TABLE appdata.root
(
    id      BIGSERIAL   NOT NULL PRIMARY KEY,
    value   VARCHAR(16) NOT NULL,
    version INT         NOT NULL DEFAULT 0,
    CONSTRAINT root_value_unique UNIQUE (value)
);
--rollback DROP TABLE appdata.root;

--changeset almi:v1-create-verb
CREATE TABLE appdata.verb
(
    id        BIGSERIAL    NOT NULL PRIMARY KEY,
    value     VARCHAR(255) NOT NULL,
    version   INT          NOT NULL DEFAULT 0,
    root_id   BIGINT       NOT NULL,
    binyan_id BIGINT       NOT NULL,
    CONSTRAINT verb_value_unique UNIQUE (value),
    CONSTRAINT fk_verb_root_id__id FOREIGN KEY (root_id) REFERENCES appdata.root (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_verb_binyan_id__id FOREIGN KEY (binyan_id) REFERENCES appdata.binyan (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verb;

--changeset almi:v1-create-verb-gizrah
CREATE TABLE appdata.verb_gizrah
(
    verb_id   BIGINT NOT NULL,
    gizrah_id BIGINT NOT NULL,
    CONSTRAINT verb_gizrah_pk PRIMARY KEY (verb_id, gizrah_id),
    CONSTRAINT fk_verb_gizrah_verb_id__id FOREIGN KEY (verb_id) REFERENCES appdata.verb (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_verb_gizrah_gizrah_id__id FOREIGN KEY (gizrah_id) REFERENCES appdata.gizrah (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verb_gizrah;

--changeset almi:v1-create-verb-preposition
CREATE TABLE appdata.verb_preposition
(
    verb_id        BIGINT NOT NULL,
    preposition_id BIGINT NOT NULL,
    CONSTRAINT verb_preposition_pk PRIMARY KEY (verb_id, preposition_id),
    CONSTRAINT fk_verb_preposition_verb_id__id FOREIGN KEY (verb_id) REFERENCES appdata.verb (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_verb_preposition_preposition_id__id FOREIGN KEY (preposition_id) REFERENCES appdata.preposition (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verb_preposition;

--changeset almi:v1-create-verbtranslate
CREATE TABLE appdata.verbtranslate
(
    id      BIGSERIAL   NOT NULL PRIMARY KEY,
    value   VARCHAR(64) NOT NULL,
    lang    VARCHAR(16) NOT NULL,
    verb_id BIGINT      NOT NULL,
    version INT         NOT NULL DEFAULT 0,
    CONSTRAINT fk_verbtranslate_verb_id__id FOREIGN KEY (verb_id) REFERENCES appdata.verb (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verbtranslate;

--changeset almi:v1-create-verbform
CREATE TABLE appdata.verbform
(
    id        BIGSERIAL    NOT NULL PRIMARY KEY,
    verb_id   BIGINT       NOT NULL,
    value     VARCHAR(255) NOT NULL,
    tense     VARCHAR(32)  NOT NULL,
    person    VARCHAR(32)  NOT NULL,
    plurality VARCHAR(32)  NOT NULL,
    gender    VARCHAR(32)  NOT NULL,
    version   INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_verbform_verb_id__id FOREIGN KEY (verb_id) REFERENCES appdata.verb (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verbform;

--changeset almi:v1-create-verbformtranslit
CREATE TABLE appdata.verbformtranslit
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    verbform_id BIGINT       NOT NULL,
    value       VARCHAR(255) NOT NULL,
    lang        VARCHAR(16)  NOT NULL,
    version     INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_verbformtranslit_verbform_id__id FOREIGN KEY (verbform_id) REFERENCES appdata.verbform (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verbformtranslit;

--changeset almi:v1-create-verbformexample
CREATE TABLE appdata.verbformexample
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    verbform_id BIGINT       NOT NULL,
    value       VARCHAR(255) NOT NULL,
    file_id     VARCHAR(255),
    version     INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_verbformexample_verbform_id__id FOREIGN KEY (verbform_id) REFERENCES appdata.verbform (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verbformexample;

--changeset almi:v1-create-verbformexampletranslate
CREATE TABLE appdata.verbformexampletranslate
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    example_id BIGINT       NOT NULL,
    lang       VARCHAR(16)  NOT NULL,
    value      VARCHAR(255) NOT NULL,
    version    INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_verbformexampletranslate_example_id__id FOREIGN KEY (example_id) REFERENCES appdata.verbformexample (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--rollback DROP TABLE appdata.verbformexampletranslate;
