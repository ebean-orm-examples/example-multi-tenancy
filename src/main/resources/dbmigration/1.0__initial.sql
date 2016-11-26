-- apply changes
create table content (
  id                            bigserial not null,
  title                         varchar(255),
  byline                        varchar(255),
  body                          varchar(255),
  version                       bigint not null,
  constraint pk_content primary key (id)
);

