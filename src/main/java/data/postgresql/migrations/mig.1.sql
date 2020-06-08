CREATE TABLE "public"."admin_users" (
    "id" serial,
    "username" varchar(255) NOT NULL,
    "pass_hash" varchar(64) NOT NULL,
    "salt" varchar(64) NOT NULL,
    "creation_timestamp" timestamp without time zone NOT NULL DEFAULT now(),
    PRIMARY KEY ("id")
);
