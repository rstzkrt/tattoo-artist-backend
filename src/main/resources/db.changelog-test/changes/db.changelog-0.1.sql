create table ADDRESS
(
    ID                uuid not null primary key,
    CITY              CHARACTER VARYING(255),
    COUNTRY           CHARACTER VARYING(255),
    OTHER_INFORMATION CHARACTER VARYING(255),
    POSTAL_CODE       CHARACTER VARYING(255),
    STATE             CHARACTER VARYING(255),
    STREET            CHARACTER VARYING(255)
);

create table APP_USER
(
    ID                  UUID    not null
        primary key,
    AVATAR_URL          CHARACTER VARYING(255),
    AVERAGE_RATING      DOUBLE PRECISION,
    CAREER_DESCRIPTION  CHARACTER VARYING(255),
    DATE_OF_BIRTH       DATE,
    EMAIL               CHARACTER VARYING(255),
    FIRST_NAME          CHARACTER VARYING(255),
    GENDER              CHARACTER VARYING(255),
    HAS_ARTIST_PAGE     BOOLEAN not null,
    LAST_NAME           CHARACTER VARYING(255),
    PHONE_NUMBER        CHARACTER VARYING(255),
    UID                 CHARACTER VARYING(255),
    BUSINESS_ADDRESS_ID uuid,
    constraint FK95BKN8Y8WL7QYDLGBBCOF7HGQ
        foreign key (BUSINESS_ADDRESS_ID) references ADDRESS
);

create table APP_USER_FAVOURITE_ARTISTS
(
    USER_ID              uuid not null,
    FAVOURITE_ARTISTS_ID uuid not null,
    constraint FK7BW1IXK2CREDT9GTF74Q0LF1J
        foreign key (FAVOURITE_ARTISTS_ID) references APP_USER,
    constraint FKCJ15O39O9U3KV8HS1Y2IDX5H3
        foreign key (USER_ID) references APP_USER
);

create table COMMENT
(
    ID             uuid not null
        primary key,
    MESSAGE        CHARACTER VARYING(255),
    POST_DATE      DATE,
    RATE           NUMERIC(19, 2),
    POSTED_BY_ID   uuid,
    TATTOO_WORK_ID uuid,
    constraint FKBD4NF7M98BDXEOPTW28QNT837
        foreign key (POSTED_BY_ID) references APP_USER,
    check (("RATE" <= CAST(5 AS NUMERIC(1)))
        AND ("RATE" >= CAST(1 AS NUMERIC(1))))
);

create table REVIEW
(
    ID           uuid not null
        primary key,
    MESSAGE      CHARACTER VARYING(255),
    REVIEW_TYPE  INTEGER,
    POSTED_BY_ID uuid,
    RECEIVER_ID  uuid,
    constraint FKKU7L1VU0Y44WGJ65PA9LEK18J
        foreign key (RECEIVER_ID) references APP_USER,
    constraint FKR71W7RRQ86F4LFB7CF3J31YBY
        foreign key (POSTED_BY_ID) references APP_USER
);

create table TATTOO_WORK
(
    ID                    uuid    not null
        primary key,
    CONVERTED_PRICE_VALUE NUMERIC(19, 2),
    COVER_PHOTO           CHARACTER VARYING(255),
    CURRENCY              CHARACTER VARYING(255),
    DESCRIPTION           CHARACTER VARYING(255),
    PRICE                 NUMERIC(19, 2) not null,
    TATTOO_STYLE          CHARACTER VARYING(255),
    CLIENT_ID             uuid,
    COMMENT_ID            uuid,
    MADE_BY_ID            uuid,
    constraint FK7VG50VLLI4NN69QV0B3LN5K1X
        foreign key (CLIENT_ID) references APP_USER,
    constraint FK82SL2GCJUQ6V9WJEELWL337PU
        foreign key (MADE_BY_ID) references APP_USER,
    constraint FKSPXJLILUF6Y148RJLBBAS3V1D
        foreign key (COMMENT_ID) references COMMENT
            on delete cascade
);

create table APP_USER_FAVORITE_TATTOO_WORKS
(
    FAVORITE_USER_LIST_ID    uuid not null,
    FAVORITE_TATTOO_WORKS_ID uuid not null,
    constraint FK3DF4UGXEQ7YORLQ5NJASVUGMJ
        foreign key (FAVORITE_USER_LIST_ID) references APP_USER,
    constraint FKOITS60WM1HOXBOAFG3PY9RSTM
        foreign key (FAVORITE_TATTOO_WORKS_ID) references TATTOO_WORK
);

alter table COMMENT
    add constraint FKBI6AVIUSPP5YKIIX1E16AGQAN
        foreign key (TATTOO_WORK_ID) references TATTOO_WORK;

create table TATTOO_WORK_DISLIKER_IDS
(
    DISLIKED_TATTOO_WORKS_ID uuid not null,
    DISLIKER_IDS_ID          uuid not null,
    constraint FK258X280WH8LMLC2DM9VDKQ3D5
        foreign key (DISLIKER_IDS_ID) references APP_USER,
    constraint FKG4LVP7Q8PVPG4NGL7T8086XW3
        foreign key (DISLIKED_TATTOO_WORKS_ID) references TATTOO_WORK
);

create table TATTOO_WORK_LIKER_IDS
(
    LIKED_TATTOO_WORKS_ID uuid not null,
    LIKER_IDS_ID          uuid not null,
    constraint FK7GBAE8C4GGRUSNN8KGRGTLU32
        foreign key (LIKER_IDS_ID) references APP_USER,
    constraint FKMQ6IBDYUVF2TK9GRP7RDMYQKE
        foreign key (LIKED_TATTOO_WORKS_ID) references TATTOO_WORK
);

create table TATTOO_WORK_PHOTOS
(
    TATTOO_WORK_ID uuid not null,
    PHOTOS         CHARACTER VARYING(255),
    constraint FKG6CY9FL0DLIT4ORQRW8PBH79K
        foreign key (TATTOO_WORK_ID) references TATTOO_WORK
);

create table TATTOO_WORK_REPORT
(
    ID                          uuid not null
        primary key,
    DATE                        DATE,
    DESCRIPTION                 CHARACTER VARYING(255),
    REPORTED_TATTOO_WORK_ID     uuid,
    TATTOO_WORK_REPORT_OWNER_ID uuid,
    constraint FK30QI6CVV3X7TL6YX9GJVPVJFS
        foreign key (TATTOO_WORK_REPORT_OWNER_ID) references APP_USER,
    constraint FK84FIHUOYJCP2VUW5RN7C69MQ1
        foreign key (REPORTED_TATTOO_WORK_ID) references TATTOO_WORK
);

create table USER_LANGUAGES
(
    USER_ID   uuid not null,
    LANGUAGES CHARACTER VARYING(255),
    constraint FKSAYFMXVI5JLDAN4VMJ16MO9YE
        foreign key (USER_ID) references APP_USER
);

create table USER_REPORT
(
    ID               uuid not null
        primary key,
    DATE             DATE,
    DESCRIPTION      CHARACTER VARYING(255),
    REPORT_OWNER_ID  uuid,
    REPORTED_USER_ID uuid,
    constraint FK7783N64DACC54NG0D1CGHPLKF
        foreign key (REPORT_OWNER_ID) references APP_USER,
    constraint FKQK4H5RQ517RCRJ81CYRUP51KA
        foreign key (REPORTED_USER_ID) references APP_USER
);

create table USER_TATTOO_STYLES
(
    USER_ID       uuid not null,
    TATTOO_STYLES CHARACTER VARYING(255),
    constraint FKQ91GESGVUURWRWBI8F22EKS6X
        foreign key (USER_ID) references APP_USER
);

create table USER_WORKING_DAYS_LIST
(
    USER_ID           uuid not null,
    WORKING_DAYS_LIST INTEGER,
    constraint FKL068S4LHFWXM75X46U89OWXGR
        foreign key (USER_ID) references APP_USER
);

