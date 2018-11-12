create table users (
  id int not null auto_increment primary key,
  username varchar(60) not null,
  password varchar(60) not null,
  enabled  int not null default 1
);

create table authorities (
  id int not null auto_increment primary key,
  user_id  int(60) not null,
  authority varchar(60) not null,
  constraint user_id foreign key (user_id) references users (id)
);

create unique index auth_unique_index
  on authorities (auth_user, authority);

