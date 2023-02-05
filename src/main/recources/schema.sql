create table IF NOT EXISTS RATING_MPA
(
    RATING_MPA_ID INTEGER auto_increment,
    NAME          CHARACTER VARYING not null,
    constraint RATING_MPA_PK
        primary key (RATING_MPA_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID         INTEGER auto_increment,
    NAME            CHARACTER VARYING not null,
    DESCRIPTION     CHARACTER VARYING(200),
    RELEASE_DATE    DATE,
    DURATION        INTEGER,
    "RATING_MPA_id" INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_RATING_MPA_RATING_MPA_ID_FK
        foreign key ("RATING_MPA_id") references RATING_MPA
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment,
    E_MAIL   CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null,
    NAME     CHARACTER VARYING,
    BIRTHDAY DATE,
    constraint USERS_PK
        primary key (USER_ID)
);

create unique index IF NOT EXISTS USERS_USER_ID
    on USERS (USER_ID);

create table IF NOT EXISTS FRIENDSHIP
(
    USER_ID   INTEGER,
    FRIEND_ID INTEGER,
    constraint FRIENDSHIP_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint FRIENDSHIP_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);

create unique index IF NOT EXISTS FRIENDSHIP_USER_ID_FRIEND_ID_UINDEX
    on FRIENDSHIP (USER_ID, FRIEND_ID);

create table IF NOT EXISTS GENRE
(
    GENRE_ID INTEGER auto_increment,
    NAME     CHARACTER VARYING,
    constraint GENRE_PK
        primary key (GENRE_ID)
);

create table IF NOT EXISTS GENRE_FILMS
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    constraint GENRE_FILMS_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint GENRE_FILMS_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);

create unique index IF NOT EXISTS GENRE_FILMS_FILM_ID_GENRE_ID_UINDEX
    on GENRE_FILMS (FILM_ID, GENRE_ID);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER,
    USER_ID INTEGER,
    constraint LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create index IF NOT EXISTS LIKES_FILM_ID_USER_ID_INDEX
    on LIKES (FILM_ID, USER_ID);