# Repository Guidelines

## Project Structure & Module Organization
本仓库是 `Java 21 + Spring Boot 3 + MyBatis-Plus` 的仓储管理后端。业务代码位于 `src/main/java/com/zhb/wms2`，按模块拆分为 `module/base`、`module/inbound`、`module/outbound`、`module/inventory`、`module/product`，典型分层为 `controller`、`service`、`service/impl`、`mapper`、`model/entity`、`model/query`。MyBatis XML 位于 `src/main/resources/mapper/<module>`，应与 Java Mapper 一一对应。运行配置在 `src/main/resources/application*.yml`，建表与初始化脚本在 `sql/`，界面示例截图在 `screenshots/`。

## Build, Test, and Development Commands
使用 Maven Wrapper，避免依赖本机 Maven 版本差异：

- `./mvnw spring-boot:run`：启动本地服务，默认读取 `application.yml` 中的 `dev` 配置。
- `./mvnw clean package`：编译、打包并生成可执行 Jar。
- `./mvnw test`：执行测试；当前仓库尚无 `src/test`，新增功能时应同时补测试。
- `./mvnw clean package -DskipTests`：仅在确认测试不受影响时用于快速打包。

开发后可访问 `http://localhost:10022/swagger-ui.html` 验证接口。

## Coding Style & Naming Conventions
Java 使用 4 空格缩进，包名全小写，类名使用 PascalCase。遵循现有后缀约定：控制器用 `*Controller`，服务接口用 `*Service`，实现类用 `*ServiceImpl`，数据访问层用 `*Mapper`，查询对象用 `*Query`，实体类放在 `model/entity`。优先沿用 Lombok（仓库已启用链式访问器），避免重复样板代码。REST 路径保持小写加连字符，例如 `/base/product-categories`。

## Testing Guidelines
当前未配置测试目录，提交新逻辑时至少补充 `src/test/java` 下的单元测试或集成测试。测试类名建议与被测类对应，如 `ProductCategoryServiceTest`。涉及 Mapper、SQL 或分页逻辑时，优先覆盖查询条件、空结果和边界值。

## Commit & Pull Request Guidelines
现有 Git 历史几乎只有 `INIT`，说明仓库尚未形成成熟提交规范。后续提交请使用简短祈使句，并带模块前缀，例如 `base: add product category paging`。PR 应说明变更目的、影响范围、数据库脚本是否变更；接口或查询行为调整请附上示例请求、响应或截图。

## Security & Configuration Tips
`application-dev.yml` 当前包含开发库连接信息。不要在提交中写入真实生产凭据；本地调试优先用环境隔离配置，并在修改 `sql/` 或数据源参数时同步说明兼容性影响。
