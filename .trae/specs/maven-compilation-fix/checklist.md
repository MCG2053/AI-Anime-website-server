# Checklist

## @Index注解语法修复
- [x] Danmaku.java中@Index注解的columnList属性使用正确的字符串格式

## Lombok配置修复
- [x] pom.xml中已添加maven-compiler-plugin配置
- [x] maven-compiler-plugin中已配置annotationProcessorPaths
- [x] annotationProcessorPaths包含lombok

## 编译验证
- [ ] 执行 `mvn clean compile` 成功，无错误（需在Ubuntu服务器上验证）
- [ ] 所有实体类的getter/setter方法可用（需在Ubuntu服务器上验证）
