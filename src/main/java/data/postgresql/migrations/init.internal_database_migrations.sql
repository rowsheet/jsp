CREATE TABLE "public"."internal_database_migrations" (
    "id" serial,
    "description" text NOT NULL DEFAULT '''NO DESCRIPTION''',
    "creation_timestamp" timestamp without time zone NOT NULL DEFAULT now(),
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."internal_database_migrations"
  ALTER COLUMN "id" DROP DEFAULT,
  ADD UNIQUE ("id");

INSERT INTO "public"."internal_database_migrations"("id", "description") VALUES(0, 'initialization');
