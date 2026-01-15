create table project (
    id bigint primary key,
    name varchar(255) not null,
    desc varchar(1000),
    create_time timestamp not null,
    update_time timestamp not null
);
