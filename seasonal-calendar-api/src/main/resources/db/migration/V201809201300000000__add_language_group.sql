
ALTER TABLE calendar
ADD COLUMN welcome_country text,
ADD COLUMN welcome_country_media text,
ADD COLUMN who_we_are text,
ADD COLUMN our_country text,
ADD COLUMN our_history text,
ADD COLUMN lg_logos text[],
ADD COLUMN media_links text[],
ADD COLUMN external_links text[];

ALTER TABLE calendar DROP COLUMN youtube_id;

