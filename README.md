Voting Application
===============================

Наиболее востребованные технологии /инструменты / фреймворки Java Enterprise:
Maven/ Spring Boot 2.x/ Spring Security/ JPA(Hibernate)/ REST(Jackson)/.

The application allows you to vote among the company's employees to choose a restaurant for lunch. 
Voting is held until eleven o'clock in the afternoon, after which you can see the results. 
Only a user with administrator rights can create and edit dishes, restaurants and users. 
The administrator can also get lists of restaurants with the names of the employees who voted for them.


### curl samples (application deployed at application context `api`).


#### get All Users
`curl -s http://localhost:8080/api/admin/users --user admin@gmail.com:admin`

#### get Users 1
`curl -s http://localhost:8080/api/admin/users/1 --user admin@gmail.com:admin`

#### get User 1 with votes
`curl -s http://localhost:8080/api/admin/users/1/with-votes --user admin@gmail.com:admin`

#### register User
`curl -s -i -X POST -d '{"name":"New User","email":"test@mail.ru","password":"test-password"}' -H 'Content-Type:application/json; charset=UTF-8' http://localhost:8080/api/profile`

#### get Profile
`curl -s http://localhost:8080/api/profile --user test@mail.ru:test-password`

#### get All Restaurants
`curl -s http://localhost:8080/api/admin/restaurants?date=2021-09-15 --user admin@gmail.com:admin`

#### get All Restaurants with Dishes today
`curl -s http://localhost:8080/api/voting/ --user user@yandex.ru:password`

#### get All Restaurants with Votes and Dishes
`curl -s http://localhost:8080/api/voting/results --user user@yandex.ru:password`

#### get All Restaurants with Votes and Dishes By Date
`curl -s http://localhost:8080/api/admin/restaurants/with-dishes-votes-by-date?date=2021-09-15 --user admin@gmail.com:admin`

#### get All Restaurants wiht Votes and Dishes and Users By Date
`curl -s http://localhost:8080/api/admin/restaurants/with-votes-users-by-date?date=2021-09-15 --user admin@gmail.com:admin`

#### update Restaurant
`curl -s -X PUT -d '{"name":"Обновленный Харбин"}' -H "Content-Type: application/json" http://localhost:8080/api/admin/restaurants/1 --user admin@gmail.com:admin`

#### create Restaurant
`curl -s -X POST -d '{"name":"Новый Харбин"}' -H "Content-Type:application/json;charset=UTF-8" http://localhost:8080/api/admin/restaurants --user admin@gmail.com:admin`

#### vote befote eleven
`curl -s -X POST -d "restaurant_id=2&time=10:00" -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/api/voting --user user@yandex.ru:password`

#### vote after eleven
`curl -s -X POST -d "restaurant_id=2&time=11:15" -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/api/voting --user user@yandex.ru:password`



