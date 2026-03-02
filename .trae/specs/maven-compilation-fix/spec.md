# Maven编译错误修复 Spec

## Why
Maven编译失败，共100个错误，主要原因是：1) Danmaku.java中@Index注解的columnList属性使用了错误的语法格式；2) pom.xml缺少Lombok注解处理器配置，导致Lombok无法生成getter/setter方法。

## What Changes
- 修复Danmaku.java中@Index注解的columnList属性语法（从数组格式改为逗号分隔字符串）
- 在pom.xml中添加maven-compiler-plugin配置，启用Lombok注解处理器

## Impact
- Affected specs: 无
- Affected code: 
  - `src/main/java/com/anime/website/entity/Danmaku.java`
  - `pom.xml`

## ADDED Requirements

### Requirement: @Index注解语法正确
系统应使用正确的JPA @Index注解语法，columnList属性必须使用逗号分隔的字符串格式，而非数组格式。

#### Scenario: 多列索引定义
- **WHEN** 定义包含多列的数据库索引时
- **THEN** columnList属性应使用 `"column1, column2"` 格式，而非 `{"column1", "column2"}`

### Requirement: Lombok注解处理器配置
Maven构建过程中必须正确配置Lombok注解处理器，以确保在编译时生成getter/setter等方法。

#### Scenario: Maven编译
- **WHEN** 执行 `mvn compile` 或 `mvn package` 时
- **THEN** Lombok注解应被正确处理，所有@Data注解的类应自动生成getter/setter/toString/equals/hashCode方法

## MODIFIED Requirements
无

## REMOVED Requirements
无
