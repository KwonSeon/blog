-- Scope stopword handling to this migration session; retain Korean/English ngrams.
SET @blog_previous_stopwords = @@SESSION.innodb_ft_enable_stopword;
SET SESSION innodb_ft_enable_stopword = OFF;
ALTER TABLE posts ADD FULLTEXT INDEX ft_posts_content (title,excerpt,content_md) WITH PARSER ngram;
SET SESSION innodb_ft_enable_stopword = @blog_previous_stopwords;
CREATE TABLE search_daily_stats (
 search_day DATE NOT NULL,
 language_bucket VARCHAR(8) NOT NULL,
 length_bucket VARCHAR(8) NOT NULL,
 result_bucket VARCHAR(8) NOT NULL,
 requests BIGINT NOT NULL DEFAULT 0,
 PRIMARY KEY(search_day,language_bucket,length_bucket,result_bucket)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
