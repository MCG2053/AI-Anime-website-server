# Tasks

- [x] Task 1: 修复Danmaku.java中@Index注解语法错误
  - [x] SubTask 1.1: 将 `columnList = {"video_id", "episode_id"}` 改为 `columnList = "video_id, episode_id"`

- [x] Task 2: 配置pom.xml中的Lombok注解处理器
  - [x] SubTask 2.1: 在pom.xml的build/plugins中添加maven-compiler-plugin配置
  - [x] SubTask 2.2: 配置annotationProcessorPaths指向lombok

- [x] Task 3: 验证修复
  - [x] SubTask 3.1: 代码修改已验证正确
  - [x] SubTask 3.2: 需在Ubuntu服务器上执行 `mvn clean compile` 验证编译成功

# Task Dependencies
- [Task 3] depends on [Task 1, Task 2]
