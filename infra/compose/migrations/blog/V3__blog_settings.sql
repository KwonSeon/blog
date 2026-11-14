CREATE TABLE blog_settings (
 id BIGINT NOT NULL PRIMARY KEY,
 site_title VARCHAR(80) NOT NULL,
 hero_title VARCHAR(160) NOT NULL,
 hero_description VARCHAR(600) NOT NULL,
 github_url VARCHAR(500) NOT NULL,
 home_order VARCHAR(30) NOT NULL,
 post_layout VARCHAR(20) NOT NULL,
 content_width VARCHAR(20) NOT NULL,
 show_hero BOOLEAN NOT NULL,
 show_posts BOOLEAN NOT NULL,
 show_projects BOOLEAN NOT NULL,
 revision BIGINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO blog_settings VALUES (1,'s-nowk','만들고, 배우고,\n기록합니다.','직접 만드는 서비스와 그 과정에서 만난 문제들. 개발의 생각과 경험을 차곡차곡 남기는 공간입니다.','https://github.com/KwonSeon','POSTS_FIRST','GRID','STANDARD',true,true,true,0);
