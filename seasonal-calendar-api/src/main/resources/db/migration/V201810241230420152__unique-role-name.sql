DELETE FROM "role" a WHERE a.CTID <> (SELECT min(b.ctid) FROM "role" b WHERE a.name = b.name);

ALTER TABLE "role" ADD UNIQUE("name");
ALTER TABLE "role" ALTER COLUMN "name" SET NOT NULL;