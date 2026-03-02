# Windows 本地开发部署指南

本文档专门针对 Windows 10/11 系统编写，提供完整的本地开发和部署流程。

---

## 目录

1. [系统准备](#1-系统准备)
2. [安装JDK 17](#2-安装jdk-17)
3. [安装MySQL 8.0](#3-安装mysql-80)
4. [安装Redis](#4-安装-redis)
5. [安装Maven](#5-安装-maven)
6. [配置项目](#6-配置项目)
7. [运行项目](#7-运行项目)
8. [常用维护命令](#8-常用维护命令)
9. [IDE配置](#9-ide配置)

---

## 1. 系统准备

### 1.1 安装必要工具

推荐使用以下工具：

- **终端**: Windows Terminal (从 Microsoft Store 安装)
- **包管理器**: Chocolatey 或 Scoop (可选，便于安装软件)
- **Git**: 版本控制工具
- **IDE**: IntelliJ IDEA 或 VS Code

### 1.2 安装Git

```powershell
# 方式一：使用 winget (Windows 11 自带)
winget install Git.Git

# 方式二：下载安装包
# 访问 https://git-scm.com/download/win 下载安装
```

### 1.3 安装Chocolatey（可选）

```powershell
# 以管理员身份运行 PowerShell
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

---

## 2. 安装JDK 17

### 2.1 方式一：使用安装包（推荐）

1. 下载 JDK 17：
   - Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java17
   - 或 Microsoft Build of OpenJDK: https://learn.microsoft.com/zh-cn/java/openjdk/download

2. 运行安装程序，按提示完成安装

3. 验证安装：
```powershell
java -version
# 输出: java version "17.0.x"
```

### 2.2 方式二：使用Chocolatey

```powershell
# 安装 OpenJDK 17
choco install openjdk17 -y

# 验证
java -version
```

### 2.3 配置环境变量（手动安装时需要）

1. 打开系统环境变量设置：
   - 右键"此电脑" → 属性 → 高级系统设置 → 环境变量

2. 新建系统变量：
   - 变量名：`JAVA_HOME`
   - 变量值：`C:\Program Files\Java\jdk-17`（根据实际安装路径）

3. 编辑 Path 变量，添加：
   - `%JAVA_HOME%\bin`

4. 验证：
```powershell
echo %JAVA_HOME%
java -version
javac -version
```

---

## 3. 安装MySQL 8.0

### 3.1 方式一：使用安装程序（推荐）

1. 下载 MySQL Installer：
   - https://dev.mysql.com/downloads/installer/

2. 运行安装程序，选择 "Developer Default" 或 "Server only"

3. 配置MySQL Server：
   - 设置 root 密码
   - 选择默认字符集为 utf8mb4
   - 配置为Windows服务自动启动

4. 验证安装：
```powershell
mysql -u root -p
# 输入密码后进入MySQL
```

### 3.2 方式二：使用Chocolatey

```powershell
# 安装 MySQL
choco install mysql -y

# 安装后会提示设置root密码
```

### 3.3 创建数据库和用户

```powershell
# 登录MySQL
mysql -u root -p
```

```sql
-- 创建数据库
CREATE DATABASE anime_website 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建专用用户
CREATE USER 'anime_user'@'localhost' IDENTIFIED BY 'YourPassword123!';

-- 授权
GRANT ALL PRIVILEGES ON anime_website.* TO 'anime_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

### 3.4 导入初始数据

```powershell
# 进入项目目录
cd F:\Code_Project\Trae\Anime_website_backend

# 导入数据
mysql -u anime_user -p anime_website < docs\init_data.sql

# 验证
mysql -u anime_user -p anime_website -e "SHOW TABLES;"
```

### 3.5 MySQL配置优化

编辑配置文件 `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`：

```ini
[mysqld]
# 字符集设置
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# 性能优化
innodb_buffer_pool_size = 512M
max_connections = 100

# 慢查询日志
slow_query_log = 1
slow_query_log_file = "slow.log"
long_query_time = 2
```

重启MySQL服务：
```powershell
net stop MySQL80
net start MySQL80
```

---

## 4. 安装 Redis

Windows原生不支持Redis，有以下几种方案：

### 4.1 方式一：使用Memurai（推荐）

Memurai是Redis的Windows原生替代品：

1. 下载地址：https://www.memurai.com/get-memurai

2. 运行安装程序

3. 验证：
```powershell
memurai-cli ping
# 输出: PONG
```

### 4.2 方式二：使用WSL运行Redis

1. 启用WSL：
```powershell
# 以管理员身份运行
wsl --install
```

2. 安装Ubuntu后，在WSL中安装Redis：
```bash
sudo apt update
sudo apt install redis-server
sudo service redis-server start
redis-cli ping
```

### 4.3 方式三：使用Docker运行Redis

```powershell
# 拉取Redis镜像
docker pull redis

# 运行Redis容器
docker run -d --name redis -p 6379:6379 redis

# 验证
docker exec -it redis redis-cli ping
```

### 4.4 方式四：使用旧版Windows Redis（不推荐）

从GitHub下载非官方维护的Windows版本：
- https://github.com/microsoftarchive/redis/releases

---

## 5. 安装Maven

### 5.1 方式一：使用安装包

1. 下载Maven：
   - https://maven.apache.org/download.cgi
   - 下载 Binary zip archive

2. 解压到指定目录，如 `C:\Program Files\Apache\maven`

3. 配置环境变量：
   - 新建 `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - Path 添加 `%MAVEN_HOME%\bin`

4. 验证：
```powershell
mvn -version
```

### 5.2 方式二：使用Chocolatey

```powershell
choco install maven -y

# 验证
mvn -version
```

### 5.3 配置Maven镜像（国内加速）

编辑 `%MAVEN_HOME%\conf\settings.xml` 或 `%USERPROFILE%\.m2\settings.xml`：

```xml
<settings>
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Maven Mirror</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

---

## 6. 配置项目

### 6.1 克隆项目

```powershell
# 克隆项目
git clone https://github.com/MCG2053/AI-Anime-website-backend.git

# 进入项目目录
cd AI-Anime-website-backend
```

### 6.2 创建本地配置文件

复制并编辑配置文件：

```powershell
# 复制配置模板
copy src\main\resources\application.yml src\main\resources\application-local.yml
```

编辑 `application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/anime_website?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
    username: anime_user
    password: YourPassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      idle-timeout: 300000
      connection-timeout: 20000
  
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
      timeout: 10000ms

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

jwt:
  secret: YourLocalDevSecretKeyMustBeVeryLongAndSecure2024!
  expiration: 86400000

logging:
  level:
    root: INFO
    com.anime.website: DEBUG
```

---

## 7. 运行项目

### 7.1 方式一：使用Maven命令

```powershell
# 编译项目
mvn clean compile

# 运行项目（开发模式）
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 打包项目
mvn clean package -DskipTests

# 运行jar包
java -jar target\anime-website-backend-1.0.0.jar --spring.profiles.active=local
```

### 7.2 方式二：使用IDE运行

#### IntelliJ IDEA

1. 打开项目：File → Open → 选择项目目录

2. 等待Maven导入依赖

3. 配置运行参数：
   - Run → Edit Configurations
   - 添加 Spring Boot 配置
   - Main class: `com.anime.website.AnimeWebsiteBackendApplication`
   - Active profiles: `local`

4. 点击运行按钮

#### VS Code

1. 安装扩展：
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. 打开项目文件夹

3. 在 Spring Boot Dashboard 中点击运行

### 7.3 验证运行

```powershell
# 检查端口
netstat -ano | findstr :8080

# 测试API
curl http://localhost:8080/api/videos

# 访问Swagger文档
# 浏览器打开: http://localhost:8080/swagger-ui/index.html
```

---

## 8. 常用维护命令

### 8.1 服务管理

```powershell
# MySQL服务
net stop MySQL80
net start MySQL80
net pause MySQL80
net continue MySQL80

# Redis服务（如果安装为Windows服务）
net stop Redis
net start Redis
```

### 8.2 数据库操作

```powershell
# 登录MySQL
mysql -u anime_user -p anime_website

# 备份数据库
mysqldump -u anime_user -p anime_website > backup_%date:~0,4%%date:~5,2%%date:~8,2%.sql

# 恢复数据库
mysql -u anime_user -p anime_website < backup_20240101.sql

# 查看数据库状态
mysql -u anime_user -p -e "SHOW PROCESSLIST;"
```

### 8.3 日志查看

```powershell
# 实时查看日志（PowerShell）
Get-Content -Path logs\app.log -Wait -Tail 100

# 查找错误
Select-String -Path logs\app.log -Pattern "ERROR" | Select-Object -Last 20
```

### 8.4 进程管理

```powershell
# 查看Java进程
jps -l

# 查看端口占用
netstat -ano | findstr :8080

# 结束进程（根据PID）
taskkill /PID <PID> /F

# 查看进程详情
tasklist /FI "IMAGENAME eq java.exe"
```

---

## 9. IDE配置

### 9.1 IntelliJ IDEA配置

#### 安装Lombok插件

1. File → Settings → Plugins
2. 搜索 "Lombok"
3. 安装并重启IDE

#### 启用注解处理

1. File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
2. 勾选 "Enable annotation processing"
3. 点击 Apply

#### 配置代码风格

1. File → Settings → Editor → Code Style → Java
2. 导入项目代码风格配置（如有）

### 9.2 VS Code配置

#### settings.json 配置

```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "spring-boot.ls.problem.application-properties.enabled": true,
    "editor.formatOnSave": true
}
```

---

## 10. 故障排查

### 10.1 端口被占用

```powershell
# 查找占用端口的进程
netstat -ano | findstr :8080

# 结束进程
taskkill /PID <PID> /F
```

### 10.2 MySQL连接失败

```powershell
# 检查MySQL服务状态
sc query MySQL80

# 测试连接
mysql -u root -p -h localhost

# 查看MySQL错误日志
Get-Content "C:\ProgramData\MySQL\MySQL Server 8.0\Data\<hostname>.err" -Tail 50
```

### 10.3 Redis连接失败

```powershell
# 检查Redis服务（Memurai）
sc query Memurai

# 测试连接
memurai-cli ping

# 如果使用Docker
docker ps
docker logs redis
```

### 10.4 Maven构建失败

```powershell
# 清理本地仓库缓存
mvn dependency:purge-local-repository

# 强制更新依赖
mvn clean install -U

# 检查Maven配置
mvn -X compile 2>&1 | Select-String "ERROR"
```

---

## 11. 快速启动脚本

创建 `start-dev.ps1`：

```powershell
# start-dev.ps1 - 开发环境快速启动脚本

Write-Host "===================================" -ForegroundColor Green
Write-Host "Anime Website Backend 开发环境启动" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green

# 检查MySQL
Write-Host "[1/4] 检查MySQL服务..." -ForegroundColor Yellow
$mysqlService = Get-Service -Name "MySQL80" -ErrorAction SilentlyContinue
if ($mysqlService -and $mysqlService.Status -eq "Running") {
    Write-Host "MySQL服务运行中" -ForegroundColor Green
} else {
    Write-Host "MySQL服务未运行，正在启动..." -ForegroundColor Yellow
    Start-Service -Name "MySQL80"
}

# 检查Redis
Write-Host "[2/4] 检查Redis服务..." -ForegroundColor Yellow
$redisService = Get-Service -Name "Memurai" -ErrorAction SilentlyContinue
if ($redisService -and $redisService.Status -eq "Running") {
    Write-Host "Redis服务运行中" -ForegroundColor Green
} else {
    Write-Host "Redis服务未运行，正在启动..." -ForegroundColor Yellow
    Start-Service -Name "Memurai"
}

# 编译项目
Write-Host "[3/4] 编译项目..." -ForegroundColor Yellow
mvn clean compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "编译失败!" -ForegroundColor Red
    exit 1
}

# 启动项目
Write-Host "[4/4] 启动项目..." -ForegroundColor Yellow
Write-Host "访问地址:" -ForegroundColor Cyan
Write-Host "  API: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "  Swagger: http://localhost:8080/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host ""

mvn spring-boot:run -Dspring-boot.run.profiles=local
```

使用方法：

```powershell
# 添加执行权限
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned

# 运行脚本
.\start-dev.ps1
```

---

## 12. 参考链接

- 项目地址：https://github.com/MCG2053/AI-Anime-website-backend
- 前端项目：https://github.com/MCG2053/Anime_Website
- JDK下载：https://adoptium.net/
- MySQL下载：https://dev.mysql.com/downloads/installer/
- Maven下载：https://maven.apache.org/download.cgi
- Memurai：https://www.memurai.com/
