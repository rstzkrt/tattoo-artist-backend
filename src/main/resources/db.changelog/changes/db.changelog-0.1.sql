create table if not exists address
(
    id                uuid not null
    primary key,
    city              varchar(255),
    country           varchar(255),
    other_information varchar(255),
    postal_code       varchar(255),
    state             varchar(255),
    street            varchar(255)
    );

alter table address
    owner to postgres;

create table if not exists app_user
(
    id                  uuid    not null
    primary key,
    avatar_url          varchar(255),
    average_rating      double precision,
    career_description  varchar(255),
    date_of_birth       date,
    email               varchar(255),
    first_name          varchar(255),
    gender              varchar(255),
    has_artist_page     boolean not null,
    last_name           varchar(255),
    phone_number        varchar(255),
    uid                 varchar(255),
    business_address_id uuid
    constraint fk_user_address
    references address
    );

alter table app_user
    owner to postgres;

create table if not exists app_user_favourite_artists
(
    user_id              uuid not null
    constraint fk_user_id
    references app_user,
    favourite_artists_id uuid not null
    constraint fk_favorite_artist_id
    references app_user
);

alter table app_user_favourite_artists
    owner to postgres;

create table if not exists comment
(
    id             uuid not null
    primary key,
    message        varchar(255),
    post_date      date,
    rate           numeric(19, 2)
    constraint comment_rate_check
    check ((rate <= (5)::numeric) AND (rate >= (1)::numeric)),
    posted_by_id   uuid
    constraint fk_posted_by_user_id
    references app_user,
    tattoo_work_id uuid
    );

alter table comment
    owner to postgres;

create table if not exists review
(
    id           uuid not null
    primary key,
    message      varchar(255),
    review_type  integer,
    posted_by_id uuid
    constraint fk_posted_by_user_id
    references app_user,
    receiver_id  uuid
    constraint fk_receiver_user_id
    references app_user
    );

alter table review
    owner to postgres;

create table if not exists tattoo_work
(
    id                    uuid           not null
    primary key,
    converted_price_value numeric(19, 2),
    cover_photo           varchar(255),
    currency              varchar(255),
    description           varchar(255),
    price                 numeric(19, 2) not null,
    tattoo_style          varchar(255),
    client_id             uuid
    constraint fk_client_user_id
    references app_user,
    comment_id            uuid
    constraint fk_comment_id
    references comment
    on delete cascade,
    made_by_id            uuid
    constraint fk_owner_user_id
    references app_user
    );

alter table tattoo_work
    owner to postgres;

create table if not exists app_user_favorite_tattoo_works
(
    favorite_user_list_id    uuid not null
    constraint fk_favorite_user_id
    references app_user,
    favorite_tattoo_works_id uuid not null
    constraint fk_favorite_tattoo_work_id
    references tattoo_work
);

alter table app_user_favorite_tattoo_works
    owner to postgres;

alter table comment
    add constraint fk_comment_tattoo_work_id
        foreign key (tattoo_work_id) references tattoo_work;

create table if not exists tattoo_work_disliker_ids
(
    disliked_tattoo_works_id uuid not null
    constraint fk_disliked_tattoo_works_id
    references tattoo_work,
    disliker_ids_id          uuid not null
    constraint fk_disliker_user_ids_id
    references app_user
);

alter table tattoo_work_disliker_ids
    owner to postgres;

create table if not exists tattoo_work_liker_ids
(
    liked_tattoo_works_id uuid not null
    constraint fk_liked_tattoo_works_id
    references tattoo_work,
    liker_ids_id          uuid not null
    constraint fk_liker_user_ids_id
    references app_user
);

alter table tattoo_work_liker_ids
    owner to postgres;

create table if not exists tattoo_work_photos
(
    tattoo_work_id uuid not null
    constraint fk_tattoo_work_id
    references tattoo_work,
    photos         varchar(255)
    );

alter table tattoo_work_photos
    owner to postgres;

create table if not exists tattoo_work_report
(
    id                          uuid not null
    primary key,
    date                        date,
    description                 varchar(255),
    reported_tattoo_work_id     uuid
    constraint fk_reported_tattoo_work_id
    references tattoo_work,
    tattoo_work_report_owner_id uuid
    constraint fk_tattoo_work_report_owner_id
    references app_user
    );

alter table tattoo_work_report
    owner to postgres;

create table if not exists user_languages
(
    user_id   uuid not null
    constraint fk_languages_user_id
    references app_user,
    languages varchar(255)
    );

alter table user_languages
    owner to postgres;

create table if not exists user_tattoo_styles
(
    user_id       uuid not null
    constraint fk_tattoo_styles_user_id
    references app_user,
    tattoo_styles varchar(255)
    );

alter table user_tattoo_styles
    owner to postgres;

create table if not exists user_working_days_list
(
    user_id           uuid not null
    constraint fk_user_working_days_list_user_id
    references app_user,
    working_days_list integer
);

alter table user_working_days_list
    owner to postgres;

create table if not exists user_report
(
    id               uuid not null
    primary key,
    date             date,
    description      varchar(255),
    report_owner_id  uuid
    constraint fk_report_owner_user_id
    references app_user,
    reported_user_id uuid
    constraint fk_report_reported_user_user_id
    references app_user
    );

alter table user_report
    owner to postgres;