/* rolesテーブル */
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_GENERAL');

/* usersテーブル */
INSERT IGNORE INTO users (id, name, email, password, role_id, enabled) VALUES (1, '侍 太郎', 'taro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 1, true);
INSERT IGNORE INTO users (id, name, email, password, role_id, enabled) VALUES (2, '侍 二郎', 'jiro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 1, true);

/* postsテーブル */
INSERT IGNORE INTO posts (id, user_id, title, content, created_at, updated_at) VALUES (1, 1, 'プログラミング学習1日目', '今日からプログラミング学習開始！頑張るぞ！', '2024-01-01 00:00:00', '2024-01-01 00:00:00');
INSERT IGNORE INTO posts (id, user_id, title, content, created_at, updated_at) VALUES (2, 1, 'プログラミング学習2日目', 'HTML/CSSの学習を始めようと思ったが、その前にローカル開発環境を構築しなければならないらしい。ひとまずVisual Studio Codeをインストールしてみた。早く使いこなせるようになりたい！', '2024-01-02 00:00:00', '2024-01-02 00:00:00');
INSERT IGNORE INTO posts (id, user_id, title, content, created_at, updated_at) VALUES (3, 1, 'プログラミング学習3日目', 'いよいよHTML/CSSの学習開始！まずはHTMLから。Webサイトはこんな仕組みで表示されていたのかと勉強になった。コードを書いてその通りに表示されるのが楽しい！', '2024-01-03 00:00:00', '2024-01-03 00:00:00');
INSERT IGNORE INTO posts (id, user_id, title, content, created_at, updated_at) VALUES (4, 1, 'プログラミング学習4日目', '今日はCSSを学んだ。Flexboxがとても難しかった。数をこなして慣れるしかなさそうだ。', '2024-01-04 00:00:00', '2024-01-04 00:00:00');
INSERT IGNORE INTO posts (id, user_id, title, content, created_at, updated_at) VALUES (5, 1, 'プログラミング学習5日目', '今日からはHTML/CSSの応用編！実際にWebサイトを作りながら学ぶ教材だ。基礎編で学んだことが形になっていくのはとても楽しい！', '2024-01-05 00:00:00', '2024-01-05 00:00:00');