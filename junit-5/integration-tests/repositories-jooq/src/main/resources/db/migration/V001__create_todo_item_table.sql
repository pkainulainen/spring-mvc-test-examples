CREATE TABLE enum_todo_item_status(
    id bigserial NOT NULL,
    enum_value text NOT NULL,
    value_description text NOT NULL,
    CONSTRAINT enum_todo_item_status_pk PRIMARY KEY (id),
    CONSTRAINT enum_todo_item_status_unique UNIQUE (enum_value)
);

CREATE TABLE todo_item(
    id bigserial NOT NULL,
    description text NOT NULL,
    status text NOT NULL DEFAULT 'OPEN',
    title text NOT NULL,
    CONSTRAINT todo_item_pk PRIMARY KEY (id),
    CONSTRAINT todo_item_status_fk FOREIGN KEY (status) REFERENCES enum_todo_item_status(enum_value)
);
