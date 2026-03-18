# CLAUDE.md

本文件为Claude Code (claude.ai/code)在处理本仓库代码时提供指导。

## 📋 项目概述

基于Spring Boot 3.5.4 + Java 21开发的仓库管理系统(WMS - Warehouse Management System)

### 🚀 快速命令

#### 构建运行
```bash
# 编译项目
mvn clean compile

# 开发环境运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境运行
mvn spring-boot:run

# 打包项目
mvn clean package

# 运行测试
mvn test

# 跳过测试打包
mvn clean package -DskipTests
```

#### 数据库信息
- **开发环境数据库**: wms2test
- **数据库服务器**: 192.168.110.214:3306
- ⚠️ **请勿随意切换数据库**

---

## 🏗️ 技术架构

### 核心技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.5.4 | 核心框架 |
| Java | 21 | 开发语言 |
| MyBatis-Plus | 3.5.12 | ORM框架 |
| MySQL | 8.x | 数据库 |
| Sa-Token | 1.44.0 | 认证授权 |
| Knife4j | 4.6.0 | API文档 |
| Hutool | 5.8.26 | 工具库 |
| HikariCP | - | 连接池 |
| Maven | 3.x | 构建工具 |

### 项目结构
```
src/main/java/com/zhb/wms2/
├── common/          # 通用组件
│   ├── BaseModel.java      # 基础实体类（含审计字段）
│   ├── BaseQuery.java      # 基础查询类
│   ├── R.java             # 统一响应封装
│   └── exception/         # 异常处理
├── config/          # 配置类
├── model/           # 实体类（继承BaseModel）
├── mapper/          # MyBatis Mapper接口
├── service/         # 业务逻辑层
│   └── impl/        # 业务逻辑实现
├── controller/      # 控制器层
└── util/            # 工具类
```

### 核心设计模式
- **BaseModel**: 所有实体类继承，包含审计字段和逻辑删除
- **统一响应**: 使用R<T>类封装所有API响应
- **MyBatis-Plus集成**: ServiceImpl模式简化CRUD
- **逻辑删除**: 全局配置，delete_flag字段

---

## 📦 业务模块


### 标准开发模式
每个业务模块遵循统一的开发模式：
1. **Model**: 继承BaseModel，使用@Schema注解
2. **Mapper**: 继承BaseMapper<实体>
3. **Service**: 接口继承IService<实体>，实现类继承ServiceImpl<Mapper, 实体>
4. **Controller**: RESTful风格，统一R<T>响应
5. **Query**: 继承BaseQuery，放置在model/dto包下

---

## 🔧 开发规范

### Controller层规范

#### 类级别注解
```java
@RestController
@RequestMapping("/模块名")
@Tag(name = "模块管理", description = "模块相关接口")
@RequiredArgsConstructor
@Validated
public class ModuleController {
    private final ModuleService moduleService;
}
```

#### 标准接口方法

```java
import com.zhb.wms2.common.validated.Update;

// 新增
@PostMapping
@Operation(summary = "添加模块信息")
public R<Long> save(
        @Parameter(description = "模块信息", required = true)
        @RequestBody @Validated(Save.class) Module module) {
    Long id = moduleService.addModule(module);
    return R.ok(id);
}

// 更新
@PutMapping
@Operation(summary = "修改模块信息")
public R<Void> update(
        @Parameter(description = "模块信息", required = true)
        @RequestBody @Validated(Update.class) Module module) {
    moduleService.updateModule(module);
    return R.optOk();
}

// 分页查询
@GetMapping
@Operation(summary = "分页查询模块信息")
public R<IPage<Module>> page(
        @Parameter(description = "查询条件")
        @Validated ModuleQuery query) {
    IPage<Module> result = moduleService.queryPage(query);
    return R.ok(result);
}

// 根据ID查询
@GetMapping("/{id}")
@Operation(summary = "查询模块详情")
public R<Module> getById(
        @Parameter(description = "模块ID", required = true)
        @PathVariable @NotNull @Min(1) Long id) {
    Module module = moduleService.getById(id);
    return R.ok(module);
}

// 删除
@DeleteMapping("/{id}")
@Operation(summary = "删除模块信息")
public R<Void> delete(
        @Parameter(description = "模块ID", required = true)
        @PathVariable @NotNull @Min(1) Long id) {
    moduleService.removeById(id);
    return R.optOk();
}

// 批量删除
@DeleteMapping("/batch")
@Operation(summary = "批量删除模块信息")
public R<Void> batchDelete(
        @Parameter(description = "模块ID列表", required = true)
        @RequestBody @NotEmpty List<Long> ids) {
    moduleService.removeByIds(ids);
    return R.optOk();
}
```

### Service层规范

#### 接口设计
```java
public interface ModuleService extends IService<Module> {

    /**
     * 添加模块信息
     */
    Long addModule(Module module);

    /**
     * 修改模块信息
     */
    void updateModule(Module module);

    /**
     * 分页查询模块信息
     */
    IPage<ModuleVO> queryPage(ModuleQuery query);
    
}
```

#### 实现类规范
```java
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addModule(Module module) {
        
    }

    @Override
    public IPage<ModuleVO> queryPage(ModuleQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Module> wrapper = buildQueryWrapper(query);

        // 2. 执行分页查询
        Page<Module> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<Module> modulePage = page(page, wrapper);

        // 3. 数据转换
        return modulePage.convert(this::convertToVO);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Module> buildQueryWrapper(ModuleQuery query) {
        LambdaQueryWrapper<Module> wrapper = new LambdaQueryWrapper<>();

        // 精确查询
        wrapper.eq(StrUtil.isNotBlank(query.getModuleCode()), Module::getModuleCode, query.getModuleCode())
               .eq(StrUtil.isNotBlank(query.getStatus()), Module::getStatus, query.getStatus());

        // 模糊查询
        wrapper.like(StrUtil.isNotBlank(query.getModuleName()), Module::getModuleName, query.getModuleName());

        // 时间范围
        wrapper.ge(query.getStartTime() != null, Module::getCreateTime, query.getStartTime())
               .le(query.getEndTime() != null, Module::getCreateTime, query.getEndTime());

        // 排序
        wrapper.orderByDesc(Module::getId);

        return wrapper;
    }
}
```

### Model层规范

#### 实体类
```java
@Schema(description = "模块信息表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_table")
public class Module extends BaseModel {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "模块ID")
    private Long id;

    @TableField("module_code")
    @Schema(description = "模块编码")
    @NotBlank(message = "模块编码不能为空")
    @Size(max = 50, message = "模块编码长度不能超过50个字符")
    private String moduleCode;

    @TableField("module_name")
    @Schema(description = "模块名称")
    @NotBlank(message = "模块名称不能为空")
    @Size(max = 100, message = "模块名称长度不能超过100个字符")
    private String moduleName;
}
```

#### 查询类
```java
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "模块查询条件")
public class ModuleQuery extends BaseQuery {

    @Schema(description = "模块编码")
    private String moduleCode;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
```

---

## 🎨 DTO/VO使用规范

### 📝 基本概念

| 类型 | 用途 | 生命周期 | 使用场景 |
|------|------|----------|----------|
| **Model** | 数据库实体映射 | 持久层 | 数据库操作、ORM映射 |
| **DTO** | 数据传输对象 | 传输层 | 接收前端数据、参数传递 |
| **VO** | 视图对象 | 展示层 | 返回前端数据、API响应 |
| **Query** | 查询条件对象 | 查询层 | 复杂查询条件构建 |

### 🔄 使用决策原则

#### ✅ 需要使用VO的场景
```java
// 1. 数据结构差异较大时
public class ModuleVO {
    private String statusDesc;  // 状态描述（非数据库字段）
    private String creatorName; // 关联查询的用户名
}


// 2. 需要在原基础增加额外字段
public class ModuleVO extends Module {
    private String statusDesc;  // 状态描述（非数据库字段）
    private String creatorName; // 关联查询的用户名
}

// 3. 需要数据聚合时
public class OrderSummaryVO {
    private Long orderId;
    private String orderStatus;
    private Integer totalCount;      // 订单项总数（非数据库字段）
    private BigDecimal totalAmount;  // 订单总金额（计算得出）
}
```

#### ❌ 可以不使用VO的场景
```java
// 1. 简单CRUD操作，字段基本一致
@GetMapping("/{id}")
public R<Module> getById(@PathVariable Long id) {
    // 直接返回Model，字段映射简单
    return R.ok(moduleService.getById(id));
}

// 2. 内部系统调用，不需要格式化
@GetMapping("/internal/{id}")
public R<Module> getInternalById(@PathVariable Long id) {
    // 内部调用，直接使用Model
    return R.ok(moduleService.getById(id));
}

```

#### ✅ 需要使用DTO的场景
```java
// 1. 前端数据结构与Model不一致时
public class ModuleCreateDTO {
    @NotBlank(message = "模块名称不能为空")
    private String moduleName;

    @NotBlank(message = "模块类型不能为空")
    private String moduleType;

    // 前端传递的标签数组
    private List<String> tags;

    // Model中没有的字段，需要特殊处理
    private String tempFileId;  // 临时文件ID
}

// 2. 复杂参数校验时
public class BatchUpdateDTO {
    @NotEmpty(message = "更新列表不能为空")
    @Size(max = 100, message = "一次最多更新100条")
    private List<@Validated ModuleUpdateItem> items;

    private String updateReason;  // 更新原因
}

// 3. 数据预处理时
public class ModuleImportDTO {
    private MultipartFile file;
    private Boolean skipError;
    private Boolean updateIfExists;
}
```

#### ❌ 可以不使用DTO的场景

```java
import com.zhb.wms2.common.validated.Update;

// 1. 简单的新增操作，字段一一对应
@PostMapping
public R<Long> save(@RequestBody @Validated(Save.class) Module module) {
    // 字段基本一致，直接使用Model
    return R.ok(moduleService.addModule(module));
}

// 2. 简单的更新操作
@PutMapping
public R<Void> update(@RequestBody @Validated(Update.class) Module module) {
    moduleService.updateById(module);
    return R.optOk();
}
```

### 🏗️ 命名规范

```java
// 实体类
public class Module { }

// VO类 - 体现展示用途
public class ModuleVO { }           // 标准VO
public class ModuleDetailVO { }     // 详情VO
public class ModuleListVO { }       // 列表VO
public class ModuleSummaryVO { }    // 摘要VO
public class ModuleStatisticsVO { } // 统计VO

// DTO类 - 体现传输用途
public class ModuleCreateDTO { }    // 创建DTO
public class ModuleUpdateDTO { }    // 更新DTO
public class ModuleQueryDTO { }     // 查询DTO（通常用Query替代）
public class ModuleImportDTO { }    // 导入DTO

// Query类 - 继承BaseQuery
public class ModuleQuery extends BaseQuery { }
```

### 💡 最佳实践

#### 1. 合理的选择策略
```java
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> {

    // 简单查询：直接返回Model
    public Module getById(Long id) {
        return super.getById(id);
    }

    // 复杂查询：返回VO
    public ModuleDetailVO getDetailById(Long id) {
        Module module = super.getById(id);
        ModuleDetailVO vo = new ModuleDetailVO();

        // 数据转换和补充
        BeanUtil.copyProperties(module, vo);
        vo.setStatusDesc(convertStatusDesc(module.getStatus()));
        vo.setCreatorName(getUserName(module.getCreatorId()));

        return vo;
    }

    // 分页查询：根据需求决定
    public IPage<Module> querySimplePage(ModuleQuery query) {
        // 简单分页，直接返回Model
        return super.page(new Page<>(query.getCurrent(), query.getSize()),
                         buildQueryWrapper(query));
    }

    public IPage<ModuleListVO> queryComplexPage(ModuleQuery query) {
        // 复杂分页，返回VO
        IPage<Module> ModelPage = super.page(new Page<>(query.getCurrent(), query.getSize()),
                                            buildQueryWrapper(query));
        return ModelPage.convert(this::convertToListVO);
    }
}
```

#### 2. Controller层选择
```java
@RestController
@RequestMapping("/modules")
public class ModuleController {

    // 1. 简单操作：使用Model
    @GetMapping("/{id}")
    public R<Module> getById(@PathVariable Long id) {
        return R.ok(moduleService.getById(id));
    }

    @PostMapping
    public R<Long> save(@RequestBody @Validated Module module) {
        return R.ok(moduleService.save(module));
    }

    // 2. 复杂操作：使用DTO/VO
    @GetMapping("/detail/{id}")
    public R<ModuleDetailVO> getDetail(@PathVariable Long id) {
        return R.ok(moduleService.getDetailById(id));
    }

    @PostMapping("/batch")
    public R<List<Long>> batchSave(@RequestBody @Validated ModuleBatchCreateDTO dto) {
        return R.ok(moduleService.batchSave(dto));
    }

    // 3. 混合使用：根据接口复杂度
    @GetMapping
    public R<IPage<Module>> page(@Validated ModuleQuery query) {
        // 管理接口，直接返回Model
        return R.ok(moduleService.querySimplePage(query));
    }

    @GetMapping("/app/list")
    public R<IPage<ModuleListVO>> appPage(@Validated ModuleQuery query) {
        // App接口，返回精简VO
        return R.ok(moduleService.queryComplexPage(query));
    }
}
```

#### 3. 转换工具选择
```java
@Component
public class ModuleConverter {

    // 简单转换：使用BeanUtil
    public ModuleVO convertToVO(Module module) {
        ModuleVO vo = new ModuleVO();
        BeanUtil.copyProperties(module, vo);

        // 补充字段
        vo.setStatusDesc(StatusEnum.descByCode(module.getStatus()));
        return vo;
    }

    // 复杂转换：手动处理
    public ModuleDetailVO convertToDetailVO(Module module) {
        ModuleDetailVO vo = new ModuleDetailVO();

        // 基础信息
        vo.setId(module.getId());
        vo.setModuleName(module.getModuleName());

        // 扩展信息
        vo.setPermissions(getModulePermissions(module.getId()));
        vo.setStatistics(getModuleStatistics(module.getId()));

        return vo;
    }
}
```

---

## ✅ 参数校验规范

### 常用验证注解
```java
// 空值验证
@NotNull(message = "不能为null")
@NotBlank(message = "不能为空或空白")
@NotEmpty(message = "不能为空集合")

// 字符串验证
@Size(min = 1, max = 50, message = "长度必须在1-50个字符之间")
@Pattern(regexp = "^[A-Z0-9_]+$", message = "只能包含大写字母、数字和下划线")

// 数值验证
@Min(value = 1, message = "值不能小于1")
@Max(value = 999, message = "值不能大于999")
@Range(min = 1, max = 999, message = "值必须在1-999之间")

```

### Controller参数校验
```java
@RestController
@Validated  // 类级别启用验证
public class ModuleController {

    // 请求体验证
    @PostMapping
    public R<Long> save(@RequestBody @Validated Module module) {
        return R.ok(moduleService.addModule(module));
    }

    // 路径参数验证
    @GetMapping("/{id}")
    public R<Module> getById(
            @PathVariable @NotNull(message = "ID不能为空") @Min(1) Long id) {
        return R.ok(moduleService.getById(id));
    }

    // 请求参数验证
    @GetMapping("/search")
    public R<IPage<Module>> search(
            @RequestParam(required = false)
            @Size(max = 100, message = "关键词长度不能超过100个字符") String keyword,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "页码必须大于0") Integer current) {
        // 查询逻辑
    }

    // 集合参数验证
    @DeleteMapping("/batch")
    public R<Void> batchDelete(
            @RequestBody @NotEmpty(message = "请选择要删除的模块")
            @Size(min = 1, max = 100, message = "一次最多删除100个模块")
            List<@NotNull @Min(1) Long> ids) {
        moduleService.removeByIds(ids);
        return R.optOk();
    }
}
```

---

## 🎯 最佳实践

### 响应格式
```java
// 成功响应
return R.ok(data);        // 带数据
return R.optOk();         // 无数据成功
return R.of(200, "自定义消息", data);  // 自定义消息

// 错误响应 - Service层抛出异常
throw new BaseException("业务错误信息");

// 错误响应 - Controller层直接返回
return R.error("错误信息");
```

### 数据库操作
```java
// 使用LambdaQueryWrapper避免硬编码
LambdaQueryWrapper<Module> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Module::getCode, code);  // 推荐
// wrapper.eq("code", code);        // 不推荐

// 条件判断使用StrUtil
wrapper.eq(StrUtil.isNotBlank(query.getCode()), Module::getCode, query.getCode());

// 批量操作
List<Module> modules = list(wrapper);      // 查询列表
boolean result = removeByIds(ids);         // 批量删除
boolean saved = saveBatch(modules);        // 批量保存
```

### 查询构建
```java
private LambdaQueryWrapper<Module> buildQueryWrapper(ModuleQuery query) {
    return new LambdaQueryWrapper<Module>()
            .eq(StrUtil.isNotBlank(query.getCode()), Module::getCode, query.getCode())
            .like(StrUtil.isNotBlank(query.getName()), Module::getName, query.getName())
            .ge(query.getStartTime() != null, Module::getCreateTime, query.getStartTime())
            .le(query.getEndTime() != null, Module::getCreateTime, query.getEndTime())
            .orderByDesc(Module::getId);
}
```

---



## 🔍 开发检查清单

### 新模块开发 checklist
- [ ] 实体类继承BaseModel，使用@Schema和@TableName注解
- [ ] 查询类继承BaseQuery，包含分页参数
- [ ] Controller使用标准RESTful风格和统一注解
- [ ] Service接口继承IService，实现类继承ServiceImpl
- [ ] 使用@Validated进行参数校验
- [ ] **DTO/VO使用决策**：根据接口复杂度合理选择使用DTO/VO
- [ ] **简单接口**：字段一一对应时可直接使用Model
- [ ] **复杂接口**：数据格式化、聚合、脱敏时使用对应的VO
- [ ] **参数接收**：前端数据结构与Model不一致时使用DTO
- [ ] 复杂业务逻辑在Service层实现
- [ ] 使用StrUtil进行字符串空值判断
- [ ] 异常使用BaseException抛出
- [ ] 查询条件使用LambdaQueryWrapper
- [ ] 添加必要的Swagger文档注解
- [ ] 考虑事务边界和数据一致性

### Mapper层 checklist
- [ ] 复杂查询方法返回完整的VO对象
- [ ] 参数使用@Param注解明确命名
- [ ] 方法注释清晰描述功能和参数
- [ ] 分页查询使用IPage<T>返回
- [ ] 使用SQL片段避免重复代码
- [ ] 动态SQL使用正确的条件判断标签

---

## 🛠️ 配置信息

### 环境配置
- **开发环境**: application-dev.yml
- **生产环境**: application-prod.yml
- **默认配置**: application.yml
- **端口**: 10000
- **应用名**: wms

### API文档
- **访问地址**: http://localhost:10000/swagger-ui.html
- **API路径**: /v3/api-docs
- **语言**: 中文


---

## ⚠️ 重要注意事项

### 数据库使用
- MCP数据库使用的是 **wms2test**
- 不要随意切换其他数据库

### Context7使用
当我需要代码生成、设置或配置步骤时，总是使用context7库或者API文档。这意味着您应该自动使用Context7 MCP解决库id和获取库文档的工具，而无需我明确询问。

### 异常处理
- Service层业务异常统一使用BaseException
- Controller层主要负责参数校验和响应转换
- 异常信息要清晰明确，便于前端处理

### 性能优化
- 分页查询避免不必要的count操作
- 复杂查询使用SQL片段复用
- 批量操作控制单次处理数量（建议不超过100条）
- 合理使用索引，避免全表扫描
