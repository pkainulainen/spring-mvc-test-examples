CREATE TABLE tag(
    id bigserial NOT NULL,
    name text NOT NULL,
    todo_item_id bigint NOT NULL,
    CONSTRAINT tag_pk PRIMARY KEY (id),
    CONSTRAINT tag_todo_item_fk FOREIGN KEY (todo_item_id) REFERENCES todo_item(id) ON DELETE CASCADE
);