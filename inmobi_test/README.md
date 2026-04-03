# Spring Boot Game Guess
## I. Giới thiệu
### Ứng dụng demo game đoán số với các tính năng
* Đăng nhập người dùng
* Chơi game đoán số
* Bảng xếp hạng (Leaderboard)
* Bảo mật với JWT + Spring Security
* Cache leaderboard bằng Redis

## II. Cấu trúc project
```
**inmobi_test**
├───src
│   ├───main
│   │   ├───java
│   │   │   └───cooccon
│   │   │       └───spring
│   │   │           │   DataInitializer.java # Data Init
│   │   │           │   InmobiTestApplication.java
│   │   │           │
│   │   │           ├───controller
│   │   │           │       GameController.java # API /guess /buy-turns /leaderboard /me
│   │   │           │
│   │   │           ├───dto
│   │   │           │       UserMeDTO.java # DTO sử dụng cho /me
│   │   │           │       UserScoreDTO.java # DTO sử dụng cho /leaderboard
│   │   │           │
│   │   │           ├───entity
│   │   │           │       Users.java # Entity Users
│   │   │           │
│   │   │           ├───repository
│   │   │           │       UserRepository.java # Chứa truy vấn DB
│   │   │           │
│   │   │           ├───security
│   │   │           │       JWTAuthenticationFilter.java # Kiểm tra hợp lệ username, password và create token
│   │   │           │       JWTAuthenticationVerficationFilter.java # Xác thực và quyền truy cập của user
│   │   │           │       SecurityConfiguration.java # Cấu hình Spring Security, tạo các @Bean sử dụng cho project
│   │   │           │       SecurityConstants.java 
│   │   │           │
│   │   │           └───service
│   │   │                   GameService.java # Xử lý logic đoán số, synchronized guess, redis cache leaderboard...
│   │   │                   UserService.java # Xử lý logic kiểm tra username, password
│   │   │
│   │   └───resources
│   │       │   application.properties # Cấu hình DB, Port, Redis...
│   │       │   data.sql # Data Sample

```
## III. Công nghệ sử dụng
| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|---------|---------|
| **Java** | 21 | Ngôn ngữ lập trình |
| **Spring Boot** | 4.0.0 | Framework web |
| **Spring Data JPA** | Mới nhất | ORM & truy cập cơ sở dữ liệu |
| **Spring Security** | Mới nhất | Xác thực & phân quyền |
| **H2 Database** | Mới nhất | Cơ sở dữ liệu trong bộ nhớ |
| **Redis** | 7.x | Lớp caching |
| **Docker** | Mới nhất | Containerization |
| **Maven** | 3.8+ | Công cụ build |

## IV. Bảo mật
1. Xử lý sao để đảm bảo tính đúng đắn của API khi user gọi /guess nhiều lần cùng lúc
   - Sử dụng synchronized trong GameService để xử lý các lệnh /guess đồng thời
2. Đảm bảo khi hệ thống có lượng user lớn, các api /leaderboard /me vẫn trả kết quả nhanh.
   - Tạo index cho 2 column(username, score) bảng user.
   - Dùng Redis hoặc cache trong memory để lưu kết quả leaderboard
   - Cập nhật định kỳ (ví dụ mỗi 1 phút hoặc khi có thay đổi điểm). 
3. Cơ chế bảo mật cho API.
   - Sử dụng JWT và login mặc định của Spring Security để xác thực và quyền truy cập dưới dạng token của các API.
## V. Sơ Đồ Cơ Sở Dữ Liệu
```
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255),
  score INT DEFAULT 0,
  turns INT DEFAULT 0
);

-- Indexes để tối ưu hiệu suất
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_score ON users(score DESC);
```

## VI. Hướng dẫn setup môi trường
### 1. Chạy Redis bằng Docker
``bash  
docker pull redis:7  
docker run -d --name redis-server -p 6379:6379 redis:7
### 2. Build bằng Maven
mvn clean install  
mvn spring-boot:run
### 3. Cách lấy token/đăng nhập
![](./inmobi_test/image/login_gettoken.png)
### 4. API Endpoints 
**URL Cơ Sở: http://localhost:8080**  
**Tất cả các endpoint (ngoại trừ /login) đều yêu cầu JWT token trong header Authorization:**  
**POST /login(Đăng Nhập & Lấy Token)**
![](./inmobi_test/image/login.PNG)  
**POST /guess(Đoán số)**  
![](./inmobi_test/image/guess.PNG)  
**POST /buy-turns(Mua thêm 5 lượt chơi cho user hiện tại)**  
![](./inmobi_test/image/buy-turns.PNG)  
**GET /leaderboard(Danh sách 10 user điểm cao nhất)**  
![](./inmobi_test/image/leaderboard.PNG)  
**GET /me(Trả về email, score, turns còn lại)**  
![](./inmobi_test/image/me.PNG)

# Spring Boot Game Guess
## I. Giới thiệu
### Ứng dụng demo game đoán số với các tính năng
* Đăng nhập người dùng
* Chơi game đoán số
* Bảng xếp hạng (Leaderboard)
* Bảo mật với JWT + Spring Security
* Cache leaderboard bằng Redis

## II. Cấu trúc project
```
**inmobi_test**
├───src
│   ├───main
│   │   ├───java
│   │   │   └───cooccon
│   │   │       └───spring
│   │   │           │   DataInitializer.java # Data Init
│   │   │           │   InmobiTestApplication.java
│   │   │           │
│   │   │           ├───controller
│   │   │           │       GameController.java # API /guess /buy-turns /leaderboard /me
│   │   │           │
│   │   │           ├───dto
│   │   │           │       UserMeDTO.java # DTO sử dụng cho /me
│   │   │           │       UserScoreDTO.java # DTO sử dụng cho /leaderboard
│   │   │           │
│   │   │           ├───entity
│   │   │           │       Users.java # Entity Users
│   │   │           │
│   │   │           ├───repository
│   │   │           │       UserRepository.java # Chứa truy vấn DB
│   │   │           │
│   │   │           ├───security
│   │   │           │       JWTAuthenticationFilter.java # Kiểm tra hợp lệ username, password và create token
│   │   │           │       JWTAuthenticationVerficationFilter.java # Xác thực và quyền truy cập của user
│   │   │           │       SecurityConfiguration.java # Cấu hình Spring Security, tạo các @Bean sử dụng cho project
│   │   │           │       SecurityConstants.java 
│   │   │           │
│   │   │           └───service
│   │   │                   GameService.java # Xử lý logic đoán số, synchronized guess, redis cache leaderboard...
│   │   │                   UserService.java # Xử lý logic kiểm tra username, password
│   │   │
│   │   └───resources
│   │       │   application.properties # Cấu hình DB, Port, Redis...
│   │       │   data.sql # Data Sample

```
## III. Công nghệ sử dụng
| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|---------|---------|
| **Java** | 21 | Ngôn ngữ lập trình |
| **Spring Boot** | 4.0.0 | Framework web |
| **Spring Data JPA** | Mới nhất | ORM & truy cập cơ sở dữ liệu |
| **Spring Security** | Mới nhất | Xác thực & phân quyền |
| **H2 Database** | Mới nhất | Cơ sở dữ liệu trong bộ nhớ |
| **Redis** | 7.x | Lớp caching |
| **Docker** | Mới nhất | Containerization |
| **Maven** | 3.8+ | Công cụ build |

## IV. Bảo mật
1. Xử lý sao để đảm bảo tính đúng đắn của API khi user gọi /guess nhiều lần cùng lúc
   - Sử dụng synchronized trong GameService để xử lý các lệnh /guess đồng thời
2. Đảm bảo khi hệ thống có lượng user lớn, các api /leaderboard /me vẫn trả kết quả nhanh.
   - Tạo index cho 2 column(username, score) bảng user.
   - Dùng Redis hoặc cache trong memory để lưu kết quả leaderboard
   - Cập nhật định kỳ (ví dụ mỗi 1 phút hoặc khi có thay đổi điểm). 
3. Cơ chế bảo mật cho API.
   - Sử dụng JWT và login mặc định của Spring Security để xác thực và quyền truy cập dưới dạng token của các API.
## V. Sơ Đồ Cơ Sở Dữ Liệu
```
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255),
  score INT DEFAULT 0,
  turns INT DEFAULT 0
);

-- Indexes để tối ưu hiệu suất
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_score ON users(score DESC);
```

## VI. Hướng dẫn setup môi trường
### 1. Chạy Redis bằng Docker
``bash  
docker pull redis:7  
docker run -d --name redis-server -p 6379:6379 redis:7
### 2. Build bằng Maven
mvn clean install  
mvn spring-boot:run
### 3. Cách lấy token/đăng nhập
![](./inmobi_test/image/login_gettoken.png)
### 4. API Endpoints 
**URL Cơ Sở: http://localhost:8080**  
**Tất cả các endpoint (ngoại trừ /login) đều yêu cầu JWT token trong header Authorization:**  
**POST /login(Đăng Nhập & Lấy Token)**
![](./inmobi_test/image/login.PNG)  
**POST /guess(Đoán số)**  
![](./inmobi_test/image/guess.PNG)  
**POST /buy-turns(Mua thêm 5 lượt chơi cho user hiện tại)**  
![](./inmobi_test/image/buy-turns.PNG)  
**GET /leaderboard(Danh sách 10 user điểm cao nhất)**  
![](./inmobi_test/image/leaderboard.PNG)  
**GET /me(Trả về email, score, turns còn lại)**  
![](./inmobi_test/image/me.PNG)

