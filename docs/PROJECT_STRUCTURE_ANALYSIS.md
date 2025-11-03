# TodayAlarm 项目结构详细分析

## 一、项目概述

**项目名称**: 定时提醒小助手 (TodayAlarm)  
**开发语言**: Kotlin  
**UI框架**: Jetpack Compose  
**架构模式**: MVVM + Clean Architecture  
**数据库**: Room (SQLite)  
**依赖注入**: Hilt/Dagger  
**最小SDK**: 34  
**编译SDK**: 36  

---

## 二、项目目录结构

```
TodayAlarm/
├── app/
│   ├── src/main/
│   │   ├── java/com/busylab/todayalarm/
│   │   │   ├── data/                          # 数据层
│   │   │   │   ├── database/                  # Room数据库
│   │   │   │   │   ├── AppDatabase.kt         # 数据库配置
│   │   │   │   │   ├── DatabaseModule.kt      # 数据库DI模块
│   │   │   │   │   ├── Converters.kt          # 类型转换器
│   │   │   │   │   ├── TestData.kt            # 测试数据
│   │   │   │   │   ├── entities/
│   │   │   │   │   │   ├── Plan.kt            # 计划实体
│   │   │   │   │   │   ├── TodoItem.kt        # 待办事项实体
│   │   │   │   │   │   └── RepeatType.kt      # 重复类型枚举
│   │   │   │   │   └── dao/
│   │   │   │   │       ├── PlanDao.kt         # 计划DAO
│   │   │   │   │       └── TodoItemDao.kt     # 待办DAO
│   │   │   │   ├── datastore/                 # DataStore配置存储
│   │   │   │   ├── mapper/                    # 数据映射器
│   │   │   │   └── repository/
│   │   │   │       ├── RepositoryModule.kt    # 仓库DI模块
│   │   │   │       ├── PlanRepositoryImpl.kt   # 计划仓库实现
│   │   │   │       ├── TodoItemRepositoryImpl.kt
│   │   │   │       └── TodoRepositoryImpl.kt
│   │   │   │
│   │   │   ├── domain/                        # 业务层
│   │   │   │   ├── model/                     # 领域模型
│   │   │   │   │   ├── Plan.kt                # 计划领域模型
│   │   │   │   │   ├── TodoItem.kt            # 待办领域模型
│   │   │   │   │   ├── TodoItemUiModel.kt     # 待办UI模型
│   │   │   │   │   ├── PlanUiModel.kt         # 计划UI模型
│   │   │   │   │   ├── WeekCalendarModel.kt   # 周历模型
│   │   │   │   │   ├── RepeatType.kt          # 重复类型
│   │   │   │   │   ├── RepeatRule.kt          # 重复规则
│   │   │   │   │   ├── ModelMapper.kt         # 模型映射
│   │   │   │   │   ├── RepeatTypeConverter.kt # 类型转换
│   │   │   │   │   └── OperationResult.kt     # 操作结果
│   │   │   │   ├── repository/                # 仓库接口
│   │   │   │   │   ├── PlanRepository.kt
│   │   │   │   │   ├── TodoItemRepository.kt
│   │   │   │   │   └── TodoRepository.kt
│   │   │   │   ├── usecase/                   # 用例
│   │   │   │   │   ├── plan/                  # 计划用例
│   │   │   │   │   ├── todo/                  # 待办用例
│   │   │   │   │   ├── calendar/              # 日历用例
│   │   │   │   │   ├── repeat/                # 重复用例
│   │   │   │   │   ├── sync/                  # 同步用例
│   │   │   │   │   └── validation/            # 验证用例
│   │   │   │   ├── exception/                 # 异常定义
│   │   │   │   └── service/                   # 业务服务
│   │   │   │
│   │   │   ├── ui/                            # 表现层
│   │   │   │   ├── screens/                   # 页面
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── AddPlanScreen.kt
│   │   │   │   │   ├── EditPlanScreen.kt
│   │   │   │   │   ├── PlanListScreen.kt
│   │   │   │   │   └── TodoListScreen.kt
│   │   │   │   ├── components/                # UI组件
│   │   │   │   │   ├── calendar/
│   │   │   │   │   │   └── WeekCalendarView.kt
│   │   │   │   │   ├── input/
│   │   │   │   │   │   ├── DateTimePicker.kt
│   │   │   │   │   │   └── RepeatSettings.kt
│   │   │   │   │   ├── plan/
│   │   │   │   │   │   └── PlanCard.kt
│   │   │   │   │   └── todo/
│   │   │   │   │       └── TodoItemCard.kt
│   │   │   │   ├── viewmodel/                 # ViewModel
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   ├── AddPlanViewModel.kt
│   │   │   │   │   ├── EditPlanViewModel.kt
│   │   │   │   │   └── PlanListViewModel.kt
│   │   │   │   ├── state/                     # UI状态
│   │   │   │   │   └── UiState.kt             # 所有UI状态定义
│   │   │   │   ├── theme/                     # 主题
│   │   │   │   │   ├── Color.kt               # 颜色定义
│   │   │   │   │   ├── Type.kt                # 字体定义
│   │   │   │   │   ├── Shape.kt               # 形状定义
│   │   │   │   │   └── Theme.kt               # 主题组合
│   │   │   │   ├── navigation/                # 导航
│   │   │   │   │   ├── Screen.kt              # 路由定义
│   │   │   │   │   └── TodayAlarmNavigation.kt
│   │   │   │   ├── permission/                # 权限管理
│   │   │   │   └── utils/                     # UI工具
│   │   │   │
│   │   │   ├── system/                        # 系统集成层
│   │   │   │   ├── alarm/                     # 闹钟管理
│   │   │   │   │   ├── AlarmScheduler.kt      # 接口
│   │   │   │   │   └── AlarmSchedulerImpl.kt   # 实现
│   │   │   │   ├── notification/              # 通知管理
│   │   │   │   │   ├── NotificationManager.kt
│   │   │   │   │   └── NotificationManagerImpl.kt
│   │   │   │   ├── receiver/                  # 广播接收器
│   │   │   │   │   ├── AlarmReceiver.kt       # 闹钟接收器
│   │   │   │   │   ├── BootReceiver.kt        # 启动接收器
│   │   │   │   │   └── NotificationActionReceiver.kt
│   │   │   │   ├── permission/                # 权限管理
│   │   │   │   │   └── PermissionManager.kt
│   │   │   │   ├── work/                      # 后台任务
│   │   │   │   │   ├── WorkScheduler.kt
│   │   │   │   │   └── SyncWorker.kt
│   │   │   │   └── worker/                    # Worker实现
│   │   │   │
│   │   │   ├── di/                            # 依赖注入
│   │   │   │   └── SystemModule.kt
│   │   │   │
│   │   │   ├── utils/                         # 工具类
│   │   │   └── MainActivity.kt                # 入口Activity
│   │   │
│   │   ├── res/                               # 资源文件
│   │   │   ├── drawable/
│   │   │   ├── values/
│   │   │   ├── values-night/
│   │   │   ├── mipmap-*/
│   │   │   └── xml/
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts                       # 模块构建配置
│   └── src/
│       ├── androidTest/
│       └── test/
│
├── docs/                                       # 文档
│   ├── 01-技术方案.md
│   ├── 02-环境搭建.md
│   ├── 03-数据层开发.md
│   ├── 04-业务层开发.md
│   ├── 05-UI层开发.md
│   ├── 06-系统集成开发.md
│   ├── 07-待办功能架构方案.md
│   ├── 08-待办功能数据模型设计.md
│   ├── 09-待办功能业务逻辑设计.md
│   ├── 10-待办功能UI设计总体方案.md
│   ├── 11-待办列表页面UI设计.md
│   ├── 12-待办详情和编辑UI设计.md
│   ├── 13-UI组件库设计.md
│   └── 14-待办功能系统集成.md
│
├── build.gradle.kts                          # 项目构建配置
├── settings.gradle.kts                       # 项目设置
├── gradle.properties                         # Gradle属性
└── local.properties                          # 本地配置

```

---

## 三、核心数据模型

### 3.1 数据库实体

#### Plan (计划)
```kotlin
// 位置: data/database/entities/Plan.kt
@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val triggerTime: Long,              // 时间戳
    val isRepeating: Boolean = false,
    val repeatType: String,             // RepeatType.name
    val repeatInterval: Int = 1,        // 重复间隔
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
```

#### TodoItem (待办事项)
```kotlin
// 位置: data/database/entities/TodoItem.kt
@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = Plan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["planId"])]
)
data class TodoItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean = false,
    val triggerTime: Long,
    val completedAt: Long? = null,
    val createdAt: Long
)
```

### 3.2 领域模型

#### Plan (领域模型)
```kotlin
// 位置: domain/model/Plan.kt
data class Plan(
    val id: String,
    val title: String,
    val content: String,
    val triggerTime: LocalDateTime,     // 使用LocalDateTime
    val isRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
```

#### TodoItemUiModel (UI模型)
```kotlin
// 位置: domain/model/TodoItemUiModel.kt
data class TodoItemUiModel(
    val id: String,
    val planId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val triggerTime: LocalDateTime,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val formattedTime: String = "",
    val formattedDate: String = "",
    val timeAgo: String = "",           // 相对时间
    val isOverdue: Boolean = false      // 是否过期
)
```

---

## 四、分层架构详解

### 4.1 数据层 (Data Layer)

**职责**: 管理数据存储、访问和转换

**主要组件**:

| 组件 | 位置 | 职责 |
|------|------|------|
| AppDatabase | data/database/ | Room数据库配置，定义DAOs |
| DatabaseModule | data/database/ | 数据库DI配置 |
| PlanDao | data/database/dao/ | 计划数据访问 |
| TodoItemDao | data/database/dao/ | 待办事项数据访问 |
| PlanRepositoryImpl | data/repository/ | 计划仓库实现 |
| TodoRepositoryImpl | data/repository/ | 待办仓库实现 |
| RepositoryModule | data/repository/ | 仓库DI配置 |

**数据访问流程**:
```
DAO (查询) → Repository (转换) → Domain Model
```

**关键DAO方法**:

PlanDao:
- `getAllPlans()`: 获取所有计划 (Flow)
- `getActivePlans()`: 获取活跃计划 (Flow)
- `getPlanById(id)`: 根据ID获取计划
- `getPlansInTimeRange()`: 获取时间范围内的计划
- `insertPlan()`: 插入计划
- `updatePlan()`: 更新计划
- `deletePlan()`: 删除计划

TodoItemDao:
- `getAllTodoItems()`: 获取所有待办 (Flow)
- `getPendingTodoItems()`: 获取未完成待办 (Flow)
- `getCompletedTodoItems()`: 获取已完成待办 (Flow)
- `getTodoItemsByPlanId()`: 按计划ID获取待办
- `getTodoItemsInTimeRange()`: 获取时间范围内的待办
- `updateTodoItemCompletionStatus()`: 更新完成状态

### 4.2 业务层 (Domain Layer)

**职责**: 实现业务逻辑，定义用例和仓库接口

**主要组件**:

| 组件 | 位置 | 职责 |
|------|------|------|
| 领域模型 | domain/model/ | 业务实体定义 |
| 仓库接口 | domain/repository/ | 数据访问契约 |
| UseCase | domain/usecase/ | 业务用例实现 |

**关键UseCase**:

```
domain/usecase/
├── plan/
│   ├── CreatePlanUseCase.kt          # 创建计划
│   ├── UpdatePlanUseCase.kt          # 更新计划
│   ├── DeletePlanUseCase.kt          # 删除计划
│   └── GetPlansUseCase.kt            # 获取计划列表
├── todo/
│   ├── CreateTodoItemUseCase.kt      # 创建待办
│   ├── CompleteTodoItemUseCase.kt    # 完成待办
│   └── GetTodoItemsUseCase.kt        # 获取待办列表
├── calendar/
│   └── GetWeekCalendarUseCase.kt     # 获取周历
├── repeat/
│   └── CalculateNextTriggerUseCase.kt# 计算下次触发时间
├── sync/
│   └── SyncTodoItemsUseCase.kt       # 同步待办
└── validation/
    └── InputValidator.kt             # 输入验证
```

### 4.3 表现层 (Presentation Layer)

**职责**: 管理UI状态、用户交互和界面显示

**主要组件**:

| 组件 | 位置 | 职责 |
|------|------|------|
| ViewModel | ui/viewmodel/ | UI状态管理 |
| Screen | ui/screens/ | 页面实现 |
| Component | ui/components/ | 可复用UI组件 |
| UiState | ui/state/ | UI状态定义 |
| UiEvent | ui/state/ | UI事件定义 |
| Theme | ui/theme/ | 主题配置 |

**ViewModel架构**:

```
HomeViewModel
├── 状态: HomeUiState
│   ├── weekCalendar: WeekCalendarModel
│   ├── selectedDate: LocalDate
│   ├── todayTodos: List<TodoItemUiModel>
│   ├── selectedDateTodos: List<TodoItemUiModel>
│   ├── isLoading: Boolean
│   └── error: String?
├── 事件: HomeUiEvent
│   ├── DateSelected
│   ├── RefreshData
│   ├── WeekChanged
│   └── DebugNotification
└── 依赖注入
    ├── GetWeekCalendarUseCase
    ├── GetTodoItemsUseCase
    ├── PlanRepository
    └── AlarmScheduler

AddPlanViewModel
├── 状态: AddPlanUiState
│   ├── title: String
│   ├── content: String
│   ├── triggerDateTime: LocalDateTime?
│   ├── isRepeating: Boolean
│   ├── repeatType: RepeatType
│   ├── repeatInterval: Int
│   ├── validationErrors: Map<String, String>
│   └── isLoading: Boolean
└── 依赖注入
    └── CreatePlanUseCase
```

**UI状态定义** (ui/state/UiState.kt):

```kotlin
// 主页状态
data class HomeUiState(
    val weekCalendar: WeekCalendarModel? = null,
    val selectedDate: LocalDate? = null,
    val todayTodos: List<TodoItemUiModel> = emptyList(),
    val selectedDateTodos: List<TodoItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 添加计划状态
data class AddPlanUiState(
    val title: String = "",
    val content: String = "",
    val triggerDateTime: LocalDateTime? = null,
    val isRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val isSaved: Boolean = false
)

// 计划列表状态
data class PlanListUiState(
    val plans: List<PlanUiModel> = emptyList(),
    val activePlans: List<PlanUiModel> = emptyList(),
    val inactivePlans: List<PlanUiModel> = emptyList(),
    val filterType: PlanFilterType = PlanFilterType.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### 4.4 系统集成层 (System Layer)

**职责**: 与Android系统集成，处理闹钟、通知等系统功能

**主要组件**:

| 组件 | 位置 | 职责 |
|------|------|------|
| AlarmScheduler | system/alarm/ | 闹钟调度接口 |
| AlarmSchedulerImpl | system/alarm/ | AlarmManager实现 |
| NotificationManager | system/notification/ | 通知管理接口 |
| NotificationManagerImpl | system/notification/ | 通知实现 |
| AlarmReceiver | system/receiver/ | 闹钟触发接收器 |
| BootReceiver | system/receiver/ | 启动接收器 |
| PermissionManager | system/permission/ | 权限管理 |
| SyncWorker | system/work/ | 后台同步任务 |

**系统集成流程**:
```
Plan Created
    ↓
AlarmScheduler.scheduleAlarm()
    ↓
AlarmManager.setExactAndAllowWhileIdle()
    ↓
[Time Reached]
    ↓
AlarmReceiver.onReceive()
    ↓
NotificationManager.showNotification()
CreateTodoItemUseCase.invoke()
    ↓
TodoItem Created & Notification Shown
```

---

## 五、依赖注入配置

### 5.1 DI模块结构

```
di/
└── SystemModule.kt                    # 系统集成DI

data/database/
└── DatabaseModule.kt                  # 数据库DI

data/repository/
└── RepositoryModule.kt                # 仓库DI
```

### 5.2 SystemModule (di/SystemModule.kt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class SystemModule {
    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        alarmSchedulerImpl: AlarmSchedulerImpl
    ): AlarmScheduler

    @Binds
    @Singleton
    abstract fun bindNotificationManager(
        notificationManagerImpl: NotificationManagerImpl
    ): NotificationManager
}
```

### 5.3 DatabaseModule (data/database/DatabaseModule.kt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePlanDao(database: AppDatabase): PlanDao {
        return database.planDao()
    }

    @Provides
    fun provideTodoItemDao(database: AppDatabase): TodoItemDao {
        return database.todoItemDao()
    }
}
```

### 5.4 RepositoryModule (data/repository/RepositoryModule.kt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPlanRepository(
        planRepositoryImpl: PlanRepositoryImpl
    ): PlanRepository

    @Binds
    abstract fun bindTodoItemRepository(
        todoItemRepositoryImpl: TodoItemRepositoryImpl
    ): TodoItemRepository

    @Binds
    abstract fun bindTodoRepository(
        todoRepositoryImpl: TodoRepositoryImpl
    ): TodoRepository
}
```

---

## 六、UI主题系统

### 6.1 颜色定义 (ui/theme/Color.kt)

```kotlin
// 浅色主题
val Primary = Color(0xFF6750A4)
val Secondary = Color(0xFF625B71)
val Tertiary = Color(0xFF7D5260)
val Error = Color(0xFFBA1A1A)
val Background = Color(0xFFFFFBFE)
val Surface = Color(0xFFFFFBFE)

// 状态颜色
val SuccessColor = Color(0xFF4CAF50)
val WarningColor = Color(0xFFFF9800)
val InfoColor = Color(0xFF2196F3)
val OverdueColor = Color(0xFFD32F2F)
val CompletedColor = Color(0xFF388E3C)
val PendingColor = Color(0xFFF57C00)
val InactiveColor = Color(0xFF9E9E9E)

// 暗色主题
val DarkPrimary = Color(0xFFD0BCFF)
val DarkSecondary = Color(0xFFCCC2DC)
// ...
```

### 6.2 字体定义 (ui/theme/Type.kt)

```kotlin
val Typography = typography(
    displayLarge = TextStyle(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = (-0.25).sp
    ),
    // ... 其他字体样式
)
```

### 6.3 形状定义 (ui/theme/Shape.kt)

```kotlin
val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)
```

### 6.4 主题组合 (ui/theme/Theme.kt)

```kotlin
@Composable
fun TodayAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

---

## 七、导航结构

### 7.1 路由定义 (ui/navigation/Screen.kt)

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddPlan : Screen("add_plan")
    object PlanList : Screen("plan_list")
    object EditPlan : Screen("edit_plan/{planId}") {
        fun createRoute(planId: String) = "edit_plan/$planId"
    }
    object TodoList : Screen("todo_list")
}
```

### 7.2 页面流程

```
Home Screen
├── 显示本周日历
├── 显示今天的待办
├── 按钮: 计划列表 | 添加计划
└── 选择日期 → 显示该日期待办

AddPlan Screen
├── 输入标题
├── 输入内容
├── 选择提醒时间
├── 设置重复
└── 保存 → 返回Home

PlanList Screen
├── 显示所有计划
├── 过滤: 全部/活跃/非活跃
├── 编辑计划 → EditPlan
└── 删除计划

TodoList Screen
├── 显示待办事项
├── 标记完成
└── 查看详情

EditPlan Screen
├── 编辑计划信息
├── 更新提醒时间
└── 保存或删除
```

---

## 八、业务逻辑关键流程

### 8.1 创建计划流程

```
User Input (AddPlanScreen)
    ↓
AddPlanViewModel.savePlan()
    ↓
InputValidator.validate()
    ↓
CreatePlanUseCase(title, content, triggerTime, ...)
    ↓
PlanRepository.insertPlan(plan)
    ↓
PlanDao.insertPlan(entityPlan)
    ↓
AlarmScheduler.scheduleAlarm(plan)
    ↓
AlarmManager.setExactAndAllowWhileIdle()
    ↓
Success → Navigate Back
```

### 8.2 闹钟触发流程

```
[Alarm Time Reached]
    ↓
AlarmReceiver.onReceive()
    ↓
NotificationManager.showNotification()
    ↓
CreateTodoItemUseCase.invoke()
    ↓
TodoItem Created in Database
    ↓
Notification Displayed to User
    ↓
User Clicks Notification
    ↓
App Opens → TodoList Screen
```

### 8.3 待办完成流程

```
User Clicks CheckBox (TodoItemCard)
    ↓
ViewModel.completeTodoItem()
    ↓
CompleteTodoItemUseCase.invoke()
    ↓
TodoItemRepository.updateTodoItemCompletionStatus()
    ↓
TodoItemDao.updateTodoItemCompletionStatus()
    ↓
UI Updates (isCompleted = true)
```

---

## 九、关键技术点

### 9.1 数据转换层次

```
Entity (Long timestamps)
    ↓ [PlanMapper]
Domain Model (LocalDateTime)
    ↓ [ModelMapper]
UI Model (with formatted strings)
    ↓
Compose UI
```

### 9.2 异步处理

- **ViewModel**: 使用 `viewModelScope.launch` 处理异步操作
- **Repository**: 返回 `Flow<T>` 用于响应式数据流
- **DAO**: 使用 `suspend` 函数和 `Flow` 返回值
- **UseCase**: 返回 `Result<T>` 或 `Flow<T>`

### 9.3 状态管理

```
UI State Flow:
Screen UI ← StateFlow ← ViewModel ← Repository ← DAO ← Database

Event Flow:
User Interaction → ViewModel.onEvent() → Update State
```

### 9.4 依赖注入

- **Hilt**: 自动DI，使用 `@HiltViewModel`、`@Inject`
- **Singleton**: 全局单例 (Database, Repository, UseCase)
- **Module**: 分模块配置 (DatabaseModule, RepositoryModule, SystemModule)

---

## 十、现有功能集成点

### 10.1 计划功能
- ✓ 创建计划
- ✓ 编辑计划
- ✓ 删除计划
- ✓ 查看计划列表
- ✓ 设置重复提醒
- ✓ 系统闹钟集成

### 10.2 待办功能
- ✓ 创建待办事项
- ✓ 标记完成
- ✓ 查看待办列表
- ✓ 按日期筛选
- ✓ 显示过期状态
- ✓ 相对时间显示

### 10.3 UI组件
- ✓ 周历视图
- ✓ 日期时间选择器
- ✓ 计划卡片
- ✓ 待办卡片
- ✓ 重复设置组件
- ✓ Material 3 主题

### 10.4 系统集成
- ✓ AlarmManager 闹钟
- ✓ BroadcastReceiver
- ✓ NotificationManager
- ✓ WorkManager 后台任务
- ✓ 权限管理

---

## 十一、待办功能集成建议

### 11.1 数据模型已就位
- ✓ TodoItem 实体已定义
- ✓ TodoItemUiModel 已定义
- ✓ TodoRepository 接口已定义
- ✓ TodoRepositoryImpl 已实现
- ✓ TodoItemDao 已实现

### 11.2 业务逻辑已就位
- ✓ GetTodoItemsUseCase 已实现
- ✓ CreateTodoItemUseCase 已实现
- ✓ CompleteTodoItemUseCase 已实现
- ✓ 验证逻辑已实现

### 11.3 UI组件已就位
- ✓ TodoItemCard 组件已实现
- ✓ TodoItemSection 组件已实现
- ✓ 主题颜色已定义
- ✓ 导航路由已定义

### 11.4 系统集成已就位
- ✓ 闹钟调度已实现
- ✓ 通知管理已实现
- ✓ 广播接收器已实现
- ✓ DI配置已完成

### 11.5 需要补充的部分
- [ ] TodoListScreen 页面实现
- [ ] 待办详情页面
- [ ] 待办编辑功能
- [ ] 待办统计功能
- [ ] 待办导出功能

---

## 十二、文件快速查询

| 功能 | 主要文件 |
|------|---------|
| 数据库 | AppDatabase.kt, DatabaseModule.kt |
| 计划实体 | Plan.kt (entity), Plan.kt (domain) |
| 待办实体 | TodoItem.kt (entity), TodoItem.kt (domain) |
| DAO | PlanDao.kt, TodoItemDao.kt |
| 仓库 | *RepositoryImpl.kt |
| UseCase | domain/usecase/ 下各文件 |
| ViewModel | ui/viewmodel/ 下各文件 |
| 页面 | ui/screens/ 下各文件 |
| 组件 | ui/components/ 下各文件 |
| 主题 | ui/theme/ 下各文件 |
| 导航 | ui/navigation/ 下各文件 |
| 闹钟 | system/alarm/ 下各文件 |
| 通知 | system/notification/ 下各文件 |
| 广播 | system/receiver/ 下各文件 |
| DI | di/SystemModule.kt |

---

## 十三、项目依赖关键库

```gradle
// Core Android
androidx.core:core-ktx
androidx.lifecycle:lifecycle-*
androidx.activity:activity-compose

// Compose
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.compose.material.icons:material-icons-extended

// Navigation
androidx.navigation:navigation-compose

// Hilt
com.google.dagger:hilt-android
com.google.dagger:hilt-compiler

// Room
androidx.room:room-runtime
androidx.room:room-ktx
androidx.room:room-compiler

// DataStore
androidx.datastore:datastore-preferences

// WorkManager
androidx.work:work-runtime-ktx

// Serialization
org.jetbrains.kotlinx:kotlinx-serialization-json

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android

// DateTime
org.jetbrains.kotlinx:kotlinx-datetime
```

---

## 十四、总结

TodayAlarm 项目采用**MVVM + Clean Architecture**设计，具有清晰的分层结构：

1. **数据层**: Room数据库 + Repository模式
2. **业务层**: UseCase用例 + 领域模型
3. **表现层**: Compose UI + ViewModel状态管理
4. **系统层**: AlarmManager + Notification集成

项目已具备完整的计划和待办功能基础架构，所有关键组件都已实现，可以直接进行功能扩展和优化。

