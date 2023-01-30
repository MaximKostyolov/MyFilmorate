# java-filmorate
Template repository for Filmorate project.


![This is an image](https://github.com/MaximKostyolov/filmorate/blob/15d43be1d2eb4f70f582d0432ffbf96e68c942f3/schema.png?raw=true)





На рисунке приведена схема базы данных Filmorate. Основные модели - фильм и пользователь. 

В таблице Films представлены столбцы: id, название, описание, длительность, дата выхода, рейтинг Ассоциации кинокомпаний (enum). Жанры фильма и лайки пользователей выведены в отдельные таблицы, т.к. у одного фильма может быть много жанров и лайков от пользователей, также один жанр может встречаться у многих фильмов и один пользователь может поставить лайк многим фильмам. 

В таблице Users представлены столбцы: id, имя пользователя, логин, дата рождения. Дружба пользователей выведена в отдельную таблицу со столбцом типа boolean, характеризующим статус дружбы

Примеры запросов.
1) Запрос на вывод 10 самых популярных фильмов:

SELECT F.name, 
       COUNT(L.user_id) as likes
FROM films as F
LEFT JOIN likes AS L ON F.film_id = L.film_id
GROUP BY F.name,
ORDER BY COUNT(L.user_id) DESC
LIMIT(10)

2) Запрос на вывод списка друзей пользователя (id = 1):

SELECT F.friend_id
       U.name
FROM friendship as F
LEFT JOIN users as ON U.user_id = F.friend_id
WHERE F.user_id = 1 
AND F.friendship_confirmed = true
