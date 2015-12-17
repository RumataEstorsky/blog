# --- !Ups

----------------------------------------------------------------------------------

CREATE SEQUENCE comments_id_seq INCREMENT BY 1;
CREATE SEQUENCE posts_id_seq INCREMENT BY 1;

CREATE TABLE posts
(
	id BIGINT DEFAULT nextval('posts_id_seq'::regclass) NOT NULL,
	content VARCHAR(2048) NOT NULL,
	created_at TIMESTAMP DEFAULT now() NOT NULL,
	modified_at TIMESTAMP NULL,
	title VARCHAR(256) NOT NULL,
	comments_count INT NOT NULL DEFAULT 0
) WITHOUT OIDS;

ALTER TABLE posts ADD CONSTRAINT pkposts PRIMARY KEY (id);

INSERT INTO posts (id, title, content) VALUES(1, 'Title Example', 'Content Example');

----------------------------------------------------------------------------------


CREATE TABLE comments
(
	id BIGINT DEFAULT nextval('comments_id_seq'::regclass) NOT NULL,
	post_id BIGINT NOT NULL,
	content VARCHAR(500) NOT NULL,
	author VARCHAR(128) NOT NULL,
	created_at TIMESTAMP NOT NULL
) WITHOUT OIDS;

ALTER TABLE comments ADD CONSTRAINT pkcomments PRIMARY KEY (id);

ALTER TABLE comments ADD CONSTRAINT fk_comments_posts FOREIGN KEY (post_id) REFERENCES posts (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

----------------------------------------------------------------------------------

CREATE FUNCTION update_comments_count() RETURNS trigger AS $$
BEGIN
  IF (TG_OP = 'DELETE') THEN
    UPDATE posts SET comments_count = (SELECT COUNT(*) FROM comments WHERE post_id = OLD.post_id) WHERE id = OLD.post_id;
    RETURN OLD;
  ELSE
    UPDATE posts SET comments_count = (SELECT COUNT(*) FROM comments WHERE post_id = NEW.post_id) WHERE id = NEW.post_id;
    RETURN NEW;
  END IF;
END $$
LANGUAGE 'plpgsql';

CREATE TRIGGER update_comments_count AFTER INSERT OR DELETE ON comments FOR EACH ROW EXECUTE PROCEDURE update_comments_count();

----------------------------------------------------------------------------------



# --- !Downs
DROP TABLE comments;
DROP TABLE posts;

DROP SEQUENCE comments_id_seq;
DROP SEQUENCE posts_id_seq;

DROP FUNCTION IF EXISTS update_comments_count() CASCADE;
