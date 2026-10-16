create table stop (
    id varchar(64) primary key,
    name varchar(255) not null,
    lat double precision,
    lon double precision
);
create table route (
    id varchar(64) primary key,
    short_name varchar(64),
    long_name varchar(255),
    route_type int
);
create table trip (
    id varchar(64) primary key,
    route_id varchar(64) not null references route(id),
    headsign varchar(255)
);
create table stop_time (
    id bigserial primary key,
    trip_id varchar(64) not null references trip(id),
    stop_id varchar(64) not null references stop(id),
    stop_sequence int not null,
    departure_seconds int not null
);
create index idx_stop_time_stop_dep on stop_time (stop_id,
departure_seconds);