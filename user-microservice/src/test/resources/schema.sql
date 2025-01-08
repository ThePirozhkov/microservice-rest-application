create table public.user_entity
(
    id         bigint       not null
        primary key auto_increment,
    username   varchar(255) not null
        unique,
    auth_token varchar(255) not null
        unique,
    money      bigint       not null
        constraint money_ck
            check (money >= 0),
    created_at timestamp(6) not null
);

