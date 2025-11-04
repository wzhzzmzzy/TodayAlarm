# TodayAlarm 架构优化检查清单

## 📋 总览

本文档提供了 TodayAlarm 项目架构优化的详细检查清单，按照优先级和实施阶段组织，确保每个优化步骤都有明确的验收标准。

## 🚨 Phase 1: 紧急修复（1-2天）

### 1.1 合并重复转换器

#### 任务清单
- [ ] **创建统一的转换器文件**
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/data/database/converters/DatabaseConverters.kt`
  - [ ] 将 `Converters.kt` 的 `RepeatType` 转换逻辑迁移过来
  - [ ] 将 `TodoConverters.kt` 的所有转换逻辑迁移过来
  - [ ] 添加 `@TypeConverters` 注解到 `AppDatabase`

- [ ] **删除旧文件**
  - [ ] 删除 `app/src/main/java/com/busylab/todayalarm/data/database/Converters.kt`
  - [ ] 删除 `app/src/main/java/com/busylab/todayalarm/data/database/converters/TodoConverters.kt`

- [ ] **验证**
  - [ ] 项目编译通过
  - [ ] 数据库迁移正常
  - [ ] 所有转换功能正常工作

#### 验收标准
```kotlin
// 验证代码示例
@Test
fun `test database converters`() {
    // 验证所有转换器正常工作
}
```

### 1.2 修复UseCase依赖

#### 任务清单
- [ ] **识别循环依赖**
  - [ ] 分析 `CreateTodoWithPlanUseCase` 的依赖关系
  - [ ] 分析 `CreatePlanFromTodoUseCase` 的依赖关系
  - [ ] 绘制依赖关系图

- [ ] **重构依赖关系**
  - [ ] 创建 `TodoPlanCoordinator` 协调器
  - [ ] 将 UseCase 间直接依赖改为通过协调器
  - [ ] 更新 `UseCaseModule` 的依赖注入

- [ ] **验证**
  - [ ] 项目编译通过
  - [ ] 功能测试通过
  - [ ] 无循环依赖警告

#### 验收标准
```kotlin
// 重构后的结构
class TodoPlanCoordinator(
    private val createTodoUseCase: CreateTodoUseCase,
    private val createPlanUseCase: CreatePlanUseCase
) {
    suspend fun createTodoWithPlan(...) {
        // 协调逻辑，而不是直接依赖
    }
}
```

### 1.3 统一命名规范

#### 任务清单
- [ ] **重命名UseCase文件**
  - [ ] `GetTodoItemsUseCaseNew.kt` → `GetTodoItemsUseCase.kt`
  - [ ] `CompleteTodoItemUseCaseNew.kt` → `CompleteTodoItemUseCase.kt`
  - [ ] 更新所有引用

- [ ] **更新类名**
  - [ ] `GetTodoItemsUseCaseNew` → `GetTodoItemsUseCase`
  - [ ] `CompleteTodoItemUseCaseNew` → `CompleteTodoItemUseCase`
  - [ ] 更新所有import语句

- [ ] **验证**
  - [ ] 项目编译通过
  - [ ] 所有引用正确更新
  - [ ] 功能测试通过

## 🏗️ Phase 2: 架构重构（3-5天）

### 2.1 重构UseCase设计模式

#### 任务清单
- [ ] **创建统一接口**
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/usecase/core/CommandUseCase.kt`
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/usecase/core/QueryUseCase.kt`
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/usecase/core/FlowUseCase.kt`

- [ ] **重构现有UseCase**
  - [ ] 重构所有Command类型UseCase实现统一接口
  - [ ] 重构所有Query类型UseCase实现统一接口
  - [ ] 统一参数传递方式（使用Params类）

- [ ] **验证**
  - [ ] 所有UseCase实现新接口
  - [ ] 参数设计统一
  - [ ] 返回类型统一（Result<T>或Flow<T>）

#### 验收标准
```kotlin
// 统一的UseCase设计
interface CommandUseCase<in P> {
    suspend operator fun invoke(params: P): Result<Unit>
}

interface QueryUseCase<in P, out R> {
    suspend operator fun invoke(params: P): Result<R>
}

// 示例实现
class CreatePlanUseCase : CommandUseCase<CreatePlanUseCase.Params> {
    data class Params(
        val title: String,
        val content: String,
        val triggerTime: LocalDateTime
    )

    override suspend fun invoke(params: Params): Result<Unit> {
        // 实现逻辑
    }
}
```

### 2.2 创建服务层

#### 任务清单
- [ ] **创建验证服务**
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/service/ValidationService.kt`
  - [ ] 迁移所有验证逻辑到统一服务
  - [ ] 删除各UseCase中的重复验证代码

- [ ] **创建清理服务**
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/service/DataCleanupService.kt`
  - [ ] 合并 `DataSyncProcessor.cleanup()` 和 `SyncTodoPlanUseCase.cleanupExpiredData()`
  - [ ] 删除重复的清理逻辑

- [ ] **创建Todo创建服务**
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/service/TodoCreationService.kt`
  - [ ] 统一3个UseCase中的Todo创建逻辑
  - [ ] 删除重复的创建代码

- [ ] **验证**
  - [ ] 所有服务功能正常
  - [ ] 重复代码已移除
  - [ ] UseCase调用服务而非直接实现

#### 验收标准
```kotlin
// 服务层设计示例
class ValidationService {
    fun validatePlan(plan: Plan): ValidationResult {
        // 统一的验证逻辑
    }

    fun validateTodo(todo: TodoItem): ValidationResult {
        // 统一的验证逻辑
    }
}

class DataCleanupService {
    suspend fun cleanupExpiredData(): CleanupResult {
        // 统一的清理逻辑
    }
}
```

### 2.3 重组依赖注入模块

#### 任务清单
- [ ] **重构UseCaseModule**
  - [ ] 按功能分组注入（Plan、Todo、Sync）
  - [ ] 注入所有17个UseCase
  - [ ] 移除循环依赖

- [ ] **创建ServiceModule**
  - [ ] 创建新的 `app/src/main/java/com/busylab/todayalarm/di/ServiceModule.kt`
  - [ ] 注入所有服务类
  - [ ] 从ManagerModule中移除服务相关注入

- [ ] **重构ManagerModule**
  - [ ] 只保留Manager类注入
  - [ ] 移除Handler和Logger到专门模块
  - [ ] 创建专门的HandlerModule和LogModule

- [ ] **验证**
  - [ ] 依赖注入正常工作
  - [ ] 无循环依赖
  - [ ] 所有组件正确注入

#### 验收标准
```kotlin
// 重构后的模块结构
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides @Singleton
    fun providePlanUseCases(...): PlanUseCases

    @Provides @Singleton
    fun provideTodoUseCases(...): TodoUseCases

    @Provides @Singleton
    fun provideSyncUseCases(...): SyncUseCases
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides @Singleton
    fun provideValidationService(...): ValidationService

    @Provides @Singleton
    fun provideDataCleanupService(...): DataCleanupService
}
```

## 🚀 Phase 3: 长期优化（1-2周）

### 3.1 精简数据模型

#### 任务清单
- [ ] **分析字段使用情况**
  - [ ] 统计 `TodoItem` 各字段的使用频率
  - [ ] 识别未使用的字段
  - [ ] 分析字段访问模式

- [ ] **设计精简模型**
  - [ ] 创建精简版的 `TodoItem` 实体
  - [ ] 保留核心业务字段
  - [ ] 移除未使用和冗余字段

- [ ] **数据库迁移**
  - [ ] 创建数据库迁移脚本
  - [ ] 处理现有数据的迁移
  - [ ] 测试迁移过程

- [ ] **验证**
  - [ ] 数据迁移成功
  - [ ] 所有功能正常
  - [ ] 性能有所提升

#### 验收标准
```kotlin
// 精简后的TodoItem
@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String? = null,
    val title: String,
    val content: String,
    val triggerTime: Long,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 3.2 引入事件驱动架构

#### 任务清单
- [ ] **设计事件系统**
  - [ ] 创建 `DomainEvent` 基类
  - [ ] 定义具体的事件类型
  - [ ] 创建事件发布器

- [ ] **重构UseCase交互**
  - [ ] 将UseCase间直接依赖改为事件驱动
  - [ ] 实现事件监听器
  - [ ] 更新相关业务逻辑

- [ ] **验证**
  - [ ] 事件系统正常工作
  - [ ] UseCase解耦成功
  - [ ] 业务逻辑正确

#### 验收标准
```kotlin
// 事件驱动设计
sealed class DomainEvent {
    data class TodoCreated(val todo: TodoItem) : DomainEvent()
    data class TodoCompleted(val todoId: String) : DomainEvent()
    data class PlanCreated(val plan: Plan) : DomainEvent()
    data class PlanUpdated(val plan: Plan) : DomainEvent()
}

interface DomainEventPublisher {
    suspend fun publish(event: DomainEvent)
}

interface DomainEventListener {
    suspend fun onEvent(event: DomainEvent)
}
```

### 3.3 统一错误处理

#### 任务清单
- [ ] **创建全局错误处理器**
  - [ ] 创建 `app/src/main/java/com/busylab/todayalarm/domain/error/GlobalErrorHandler.kt`
  - [ ] 定义错误类型和处理策略
  - [ ] 实现用户友好的错误消息

- [ ] **重构ViewModel错误处理**
  - [ ] 移除各ViewModel中的重复错误处理逻辑
  - [ ] 统一使用全局错误处理器
  - [ ] 标准化错误状态管理

- [ ] **验证**
  - [ ] 错误处理统一
  - [ ] 用户体验改善
  - [ ] 调试信息完整

#### 验收标准
```kotlin
// 统一错误处理
class GlobalErrorHandler {
    fun handleException(e: Exception): ErrorState {
        return when (e) {
            is BusinessException -> ErrorState.Business(e.message)
            is NetworkException -> ErrorState.Network(e.message)
            else -> ErrorState.Unknown("未知错误")
        }
    }

    fun getUserMessage(e: Exception): String {
        // 返回用户友好的错误消息
    }
}
```

## 📊 质量检查指标

### 代码质量指标
- [ ] **重复代码率** < 5%
- [ ] **圈复杂度** < 10
- [ ] **测试覆盖率** > 80%
- [ ] **编译时间** < 2分钟

### 架构质量指标
- [ ] **依赖复杂度** 降低 50%
- [ ] **模块耦合度** < 30%
- [ ] **接口一致性** 100%
- [ ] **文档完整性** > 90%

## 🔧 工具和脚本

### 自动化检查脚本
```bash
#!/bin/bash
# architecture_check.sh

echo "🔍 检查架构优化进度..."

# 检查重复代码
echo "📊 检查重复代码..."
./gradlew detekt

# 检查依赖分析
echo "🔗 分析依赖关系..."
./gradlew app:dependencies

# 检查测试覆盖率
echo "🧪 检查测试覆盖率..."
./gradlew jacocoTestReport

echo "✅ 检查完成"
```

### 重构验证脚本
```bash
#!/bin/bash
# refactor_validation.sh

echo "🧪 验证重构结果..."

# 编译检查
./gradlew clean build

# 功能测试
./gradlew testDebugUnitTest

# 集成测试
./gradlew connectedDebugAndroidTest

echo "✅ 验证完成"
```

## 📝 注意事项

### 风险控制
1. **备份策略**: 每个阶段前创建完整备份
2. **分支管理**: 在独立分支进行重构
3. **测试覆盖**: 确保每个改动都有对应测试
4. **渐进式**: 优先修复阻塞问题

### 团队协作
1. **代码审查**: 每个阶段都需要团队审查
2. **文档更新**: 及时更新相关文档
3. **知识分享**: 定期分享重构经验
4. **进度跟踪**: 使用项目管理工具跟踪进度

---

**创建时间**: 2025-11-04
**适用版本**: TodayAlarm v1.0+
**维护者**: 开发团队

这个检查清单将指导团队系统性地完成 TodayAlarm 项目的架构优化工作。