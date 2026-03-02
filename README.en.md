# Anime Website Backend API

A Spring Boot 3 based backend service for anime website, providing complete features including user authentication, video management, comments, danmaku, etc.

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Security** - Security framework
- **Spring Data JPA** - Data persistence
- **MySQL** - Database
- **Redis** - Cache
- **JWT (JJWT 0.12.6)** - Authentication
- **Swagger/OpenAPI (springdoc 2.8.16)** - API documentation

## Features

### User Module
- User registration, login, logout
- JWT Token authentication
- User profile management (avatar, bio, password change)
- User statistics
- Like and collection records

### Video Module
- Video list (filter by category, year, country)
- Video details
- Video search
- Popular videos, latest videos
- Recommended videos
- Weekly schedule
- Video like and collection

### Comment Module
- Post comments
- Comment replies
- Comment likes
- Delete comments

### Danmaku Module
- Send danmaku
- Get danmaku list

### Anime List Module
- Add/remove anime list
- Anime status management
- Watch history

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [Quick Start](docs/QUICK_START.md) | 5-minute quick start guide (Windows/Linux) |
| [Operation Guide](docs/OPERATION_GUIDE.md) | Detailed configuration and usage instructions |
| [Ubuntu Deployment](docs/DEPLOY_UBUNTU.md) | Complete Ubuntu server deployment guide |
| [CentOS Deployment](docs/DEPLOY_CENTOS.md) | Complete CentOS/RHEL server deployment guide |
| [Init Data](docs/init_data.sql) | Test data SQL script |

## Quick Start

### Requirements

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### Windows Quick Start

```powershell
# 1. Clone project
git clone https://github.com/MCG2053/AI-Anime-website-backend.git
cd AI-Anime-website-backend

# 2. Create database
mysql -u root -p -e "CREATE DATABASE anime_website CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. Modify config src/main/resources/application.yml
#    Set database username and password

# 4. Start project
mvn spring-boot:run

# 5. Import test data
mysql -u root -p anime_website < docs/init_data.sql
```

### Ubuntu Quick Start

```bash
# 1. Install dependencies
sudo apt update
sudo apt install -y openjdk-17-jdk mysql-server redis-server maven

# 2. Start services
sudo systemctl start mysql redis-server

# 3. Clone project
git clone https://github.com/MCG2053/AI-Anime-website-backend.git
cd AI-Anime-website-backend

# 4. Create database
sudo mysql -e "CREATE DATABASE anime_website CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 5. Modify config and start
vim src/main/resources/application.yml
mvn spring-boot:run
```

### CentOS Quick Start

```bash
# 1. Install dependencies
sudo yum install -y epel-release
sudo yum install -y java-17-openjdk mysql-community-server redis maven

# 2. Start services
sudo systemctl start mysqld redis

# 3. Clone project
git clone https://github.com/MCG2053/AI-Anime-website-backend.git
cd AI-Anime-website-backend

# 4. Configure and start
vim src/main/resources/application.yml
mvn spring-boot:run
```

### API Documentation

After starting the project, visit:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## API Endpoints

### Basic Information
- **Base URL**: `/api`
- **Authentication**: JWT Token
- **Request Header**: `Authorization: Bearer <token>`

### Main Endpoints

| Module | Method | Path | Description |
|--------|--------|------|-------------|
| User | POST | /user/login | User login |
| User | POST | /user/register | User registration |
| User | GET | /user/info | Get user info |
| Video | GET | /videos | Get video list |
| Video | GET | /videos/{id} | Get video details |
| Video | GET | /search | Search videos |
| Comment | GET | /videos/{videoId}/comments | Get comment list |
| Comment | POST | /comments | Post comment |
| Danmaku | GET | /videos/{videoId}/danmaku | Get danmaku |
| Danmaku | POST | /danmaku | Send danmaku |

## Project Structure

```
src/main/java/com/anime/website/
├── config/          # Configuration classes
├── controller/      # Controllers
├── dto/             # Data Transfer Objects
├── entity/          # Entity classes
├── exception/       # Exception handling
├── repository/      # Data access layer
├── security/        # Security related
└── service/         # Business logic layer
```

## Response Format

### Success Response
```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

### Error Response
```json
{
  "code": 400,
  "message": "Error message",
  "data": null
}
```

## Error Codes

| Code | Description |
|------|-------------|
| 0 | Success |
| 400 | Bad Request |
| 401 | Unauthorized/Token Expired |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Server Error |

## Development Guide

### Branch Management
- `main`: Production stable branch, direct push prohibited
- `develop`: Development integration branch
- `feature/xxx`: Feature development branch
- `bugfix/xxx`: Bug fix branch

### Commit Convention
```
<type>(<scope>): <subject>

<body>
```

type:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Refactoring
- `test`: Testing
- `chore`: Build/tools

## License

[MIT License](LICENSE)

## Related Projects

- Frontend Project: [AI-Anime-website](https://github.com/MCG2053/AI-Anime-website)
