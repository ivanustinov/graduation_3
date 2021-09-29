Voting Application
===============================
Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without frontend**.

The task is:

Build a voting system for deciding where to have lunch.

* 2 types of users: admin and regular users
* Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides a new menu each day.

Admin:
name: admin@gmail.com, password: admin
User:
name: user@yandex.ru, password: password

[You can test this application in swagger](http://localhost:8080/api/swagger-ui.html)

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



