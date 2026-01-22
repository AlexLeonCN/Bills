drop table if exists project;
create table project (
    id bigint primary key,
    name varchar(255) not null,
    desc varchar(1000),
    create_time timestamp not null,
    update_time timestamp not null
);

drop table if exists bill;
create table bill (
    id bigint primary key,
    ledger varchar(255) not null,
    category varchar(255),
    sub_category varchar(255),
    currency varchar(50),
    amount decimal(18, 2),
    account varchar(255),
    recorder varchar(255),
    bill_date date,
    bill_time time,
    tag varchar(255),
    remark varchar(1000),
    income_expense varchar(50),
    create_time timestamp not null,
    update_time timestamp not null
);
