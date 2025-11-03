# TodayAlarm 项目快速参考指南

## 快速导航

### 数据模型快速查询

#### 计划 (Plan)
- 实体: `/app/src/main/java/com/busylab/todayalarm/data/database/entities/Plan.kt`
- 领域模型: `/app/src/main/java/com/busylab/todayalarm/domain/model/Plan.kt`
- DAO: `/app/src/main/java/com/busylab/todayalarm/data/database/dao/PlanDao.kt`
- 仓库接口: `/app/src/main/java/com/busylab/todayalarm/domain/repository/PlanRepository.kt`
- 仓库实现: `/app/src/main/java/com/busylab/todayalarm/data/repository/PlanRepositoryImpl.kt`

#### 待办事项 (TodoItem)
- 实体: `/app/src/main/java/com/busylab/todayalarm/data/database/entities/TodoItem.kt`
- UI模型: `/app/src/main/java/com/busylab/todayalarm/domain/model/TodoItemUiModel.kt`
- DAO: `/app/src/main/java/com/busylab/todayalarm/data/database/dao/TodoItemDao.kt`
- 仓库接口: `/app/src/main/java/com/busylab/todayalarm/domain/repository/TodoRepository.kt`
- 仓库实现: `/app/src/main/java/com/busylab/todayalarm/data/repository/TodoRepositoryImpl.kt`

### 业务逻辑快速查询

#### UseCase (用例)
- 创建计划: `domain/usecase/plan/CreatePlanUseCase.kt`
- 创建待办: `domain/usecase/todo/CreateTodoItemUseCase.kt`
- 完成待办: `domain/usecase/todo/CompleteTodoItemUseCase.kt`
- 获取待办: `domain/usecase/todo/GetTodoItemsUseCase.kt`
- 获取周历: `domain/usecase/calendar/GetWeekCalendarUseCase.kt`
- 输入验证: `domain/usecase/validation/InputValidator.kt`

### UI快速查询

#### ViewModel
- 主页: `ui/viewmodel/HomeViewModel.kt`
- 添加计划: `ui/viewmodel/AddPlanViewModel.kt`
- 编辑计划: `ui/viewmodel/EditPlanViewModel.kt`
- 计划列表: `ui/viewmodel/PlanListViewModel.kt`

#### 页面 (Screen)
- 主页: `ui/screens/HomeScreen.kt`
- 添加计划: `ui/screens/AddPlanScreen.kt`
- 编辑计划: `ui/screens/EditPlanScreen.kt`
- 计划列表: `ui/screens/PlanListScreen.kt`
- 待办列表: `ui/screens/TodoListScreen.kt`

#### 组件 (Component)
- 待办卡片: `ui/components/todo/TodoItemCard.kt`
- 计划卡片: `ui/components/plan/PlanCard.kt`
- 周历: `ui/components/calendar/WeekCalendarView.kt`
- 日期时间选择: `ui/components/input/DateTimePicker.kt`
- 重复设置: `ui/components/input/RepeatSettings.kt`

#### 主题 (Theme)
- 颜色: `ui/theme/Color.kt` (定义所有颜色)
- 字体: `ui/theme/Type.kt`
- 形状: `ui/theme/Shape.kt`
- 主题: `ui/theme/Theme.kt`

#### 状态 (State)
- 所有UI状态: `ui/state/UiState.kt`
  - HomeUiState
  - AddPlanUiState
  - PlanListUiState
  - EditPlanUiState
  - 对应的 UiEvent 接口

### 系统集成快速查询

#### 闹钟管理
- 接口: `system/alarm/AlarmScheduler.kt`
- 实现: `system/alarm/AlarmSchedulerImpl.kt`

#### 通知管理
- 接口: `system/notification/NotificationManager.kt`
- 实现: `system/notification/NotificationManagerImpl.kt`

#### 广播接收器
- 闹钟接收: `system/receiver/AlarmReceiver.kt`
- 启动接收: `system/receiver/BootReceiver.kt`
- 通知操作: `system/receiver/NotificationActionReceiver.kt`

#### 后台任务
- 工作调度: `system/work/WorkScheduler.kt`
- 同步任务: `system/work/SyncWorker.kt`

### 依赖注入快速查询

#### DI模块
- 系统模块: `di/SystemModule.kt` (绑定AlarmScheduler, NotificationManager)
- 数据库模块: `data/database/DatabaseModule.kt` (提供Database, DAO)
- 仓库模块: `data/repository/RepositoryModule.kt` (绑定Repository)

---

## 常见操作

### 1. 创建新的计划

```kotlin
// 在AddPlanViewModel中
private fun savePlan() {
    // 1. 验证输入
    val validationErrors = validateInput(state)
    if (validationErrors.isNotEmpty()) {
        _uiState.value = state.copy(validationErrors = validationErrors)
        return
    }
    
    // 2. 调用UseCase
    val result = createPlanUseCase(
        title = state.title,
        content = state.content,
        triggerTime = state.triggerDateTime!!,
        isRepeating = state.isRepeating,
        repeatType = state.repeatType,
        repeatInterval = state.repeatInterval
    )
    
    // 3. 处理结果
    result.fold(
        onSuccess = { planId ->
            _uiState.value = state.copy(isSaved = true)
            _uiEvent.emit(AddPlanUiEvent.ShowSnackbar("计划创建成功"))
            _uiEvent.emit(AddPlanUiEvent.NavigateBack)
        },
        onFailure = { error ->
            _uiState.value = state.copy(error = error.message)
        }
    )
}
```

### 2. 获取待办事项列表

```kotlin
// 在任何需要待办的地方
val getTodoItemsUseCase: GetTodoItemsUseCase // 注入

// 获取未完成的待办
getTodoItemsUseCase.getPendingTodoItems()
    .collect { todoItems ->
        // todoItems: List<TodoItemUiModel>
        // 包含格式化的时间、过期状态等
    }

// 获取已完成的待办
getTodoItemsUseCase.getCompletedTodoItems()
    .collect { todoItems ->
        // ...
    }
```

### 3. 标记待办为完成

```kotlin
// 在ViewModel中
viewModelScope.launch {
    completeTodoItemUseCase(todoItemId)
    // 自动更新UI
}
```

### 4. 添加新的UI状态

```kotlin
// 在ui/state/UiState.kt中添加
data class MyNewUiState(
    val data: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface MyNewUiEvent : UiEvent {
    data class DataChanged(val data: String) : MyNewUiEvent
    object SaveData : MyNewUiEvent
    data class ShowSnackbar(val message: String) : MyNewUiEvent
    data class ShowError(val error: String) : MyNewUiEvent
    object NavigateBack : MyNewUiEvent
}
```

### 5. 创建新的ViewModel

```kotlin
@HiltViewModel
class MyNewViewModel @Inject constructor(
    private val someUseCase: SomeUseCase,
    private val repository: SomeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MyNewUiState())
    val uiState: StateFlow<MyNewUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<MyNewUiEvent>()
    val uiEvent: SharedFlow<MyNewUiEvent> = _uiEvent.asSharedFlow()
    
    fun onEvent(event: MyNewUiEvent) {
        when (event) {
            is MyNewUiEvent.DataChanged -> updateData(event.data)
            is MyNewUiEvent.SaveData -> saveData()
            // ...
        }
    }
    
    private fun updateData(data: String) {
        _uiState.value = _uiState.value.copy(data = data)
    }
    
    private fun saveData() {
        viewModelScope.launch {
            try {
                // 调用UseCase
                someUseCase(data = _uiState.value.data)
                _uiEvent.emit(MyNewUiEvent.ShowSnackbar("保存成功"))
            } catch (e: Exception) {
                _uiEvent.emit(MyNewUiEvent.ShowError(e.message ?: "保存失败"))
            }
        }
    }
}
```

### 6. 创建新的页面

```kotlin
@Composable
fun MyNewScreen(
    viewModel: MyNewViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MyNewUiEvent.NavigateBack -> onNavigateBack()
                is MyNewUiEvent.ShowSnackbar -> {
                    // 显示提示
                }
                is MyNewUiEvent.ShowError -> {
                    // 显示错误
                }
                else -> {}
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // UI内容
        Text(uiState.data)
        
        Button(onClick = { viewModel.onEvent(MyNewUiEvent.SaveData) }) {
            Text("保存")
        }
    }
}
```

### 7. 添加新的UseCase

```kotlin
// domain/usecase/myfeature/MyNewUseCase.kt
@Singleton
class MyNewUseCase @Inject constructor(
    private val repository: MyRepository
) {
    suspend operator fun invoke(param: String): Result<String> {
        return try {
            val result = repository.doSomething(param)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 8. 添加新的Repository

```kotlin
// domain/repository/MyRepository.kt
interface MyRepository {
    suspend fun doSomething(param: String): String
}

// data/repository/MyRepositoryImpl.kt
@Singleton
class MyRepositoryImpl @Inject constructor(
    private val myDao: MyDao
) : MyRepository {
    override suspend fun doSomething(param: String): String {
        return myDao.query(param)
    }
}

// data/repository/RepositoryModule.kt (添加绑定)
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // ... 现有绑定 ...
    
    @Binds
    abstract fun bindMyRepository(
        myRepositoryImpl: MyRepositoryImpl
    ): MyRepository
}
```

---

## 数据流示例

### 从UI到数据库

```
User Input (UI)
    ↓
ViewModel.onEvent(event)
    ↓
ViewModel 更新 _uiState
    ↓
UseCase.invoke()
    ↓
Repository.insert()
    ↓
DAO.insert()
    ↓
Database (SQLite)
```

### 从数据库到UI

```
Database (SQLite)
    ↓
DAO.query() → Flow<List<Entity>>
    ↓
Repository.map() → Flow<List<DomainModel>>
    ↓
UseCase.map() → Flow<List<UiModel>>
    ↓
ViewModel.collect() → _uiState.value = newState
    ↓
UI.collectAsStateWithLifecycle() → recompose
    ↓
Display (Compose)
```

---

## 主题颜色使用

### 在Compose中使用颜色

```kotlin
// 使用Material3主题颜色
Text(
    text = "文本",
    color = MaterialTheme.colorScheme.primary
)

// 使用自定义状态颜色
Card(
    colors = CardDefaults.cardColors(
        containerColor = if (isCompleted)
            CompletedColor  // 来自Color.kt
        else
            PendingColor
    )
)
```

### 自定义颜色定义位置

所有颜色定义在 `ui/theme/Color.kt`：
- 主题颜色: Primary, Secondary, Tertiary, Error
- 状态颜色: SuccessColor, WarningColor, OverdueColor, CompletedColor, PendingColor
- 暗色主题: DarkPrimary, DarkSecondary, ...

---

## 常见问题

### Q: 如何添加新的数据表？
A: 
1. 在 `data/database/entities/` 创建新的Entity类
2. 在 `data/database/dao/` 创建对应的DAO接口
3. 在 `AppDatabase.kt` 的 `@Database` 注解中添加新的Entity
4. 在 `AppDatabase.kt` 中添加新的abstract方法返回DAO

### Q: 如何在ViewModel中注入依赖？
A:
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val useCase: MyUseCase,
    private val repository: MyRepository
) : ViewModel()
```

### Q: 如何处理异步操作？
A:
```kotlin
viewModelScope.launch {
    try {
        val result = repository.getData()  // suspend函数
        _uiState.value = _uiState.value.copy(data = result)
    } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(error = e.message)
    }
}
```

### Q: 如何更新UI状态？
A:
```kotlin
_uiState.value = _uiState.value.copy(
    field1 = newValue1,
    field2 = newValue2
)
```

### Q: 如何发送UI事件？
A:
```kotlin
viewModelScope.launch {
    _uiEvent.emit(MyUiEvent.ShowSnackbar("消息"))
}
```

### Q: 如何在Compose中收集Flow？
A:
```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()
val event = viewModel.uiEvent

LaunchedEffect(Unit) {
    event.collect { event ->
        // 处理事件
    }
}
```

---

## 项目文件大小参考

| 模块 | 文件数 | 主要职责 |
|------|-------|---------|
| data/database | 10+ | 数据库、DAO、实体 |
| data/repository | 4+ | 数据仓库实现 |
| domain/model | 10+ | 领域模型、映射器 |
| domain/usecase | 15+ | 业务用例 |
| domain/repository | 3+ | 仓库接口 |
| ui/screens | 5+ | 页面 |
| ui/components | 5+ | UI组件 |
| ui/viewmodel | 4+ | ViewModel |
| ui/theme | 4+ | 主题配置 |
| system | 8+ | 系统集成 |
| di | 3+ | 依赖注入 |

---

## 编码规范

### 命名规范
- Entity类: `Plan`, `TodoItem` (名词)
- DAO接口: `PlanDao`, `TodoItemDao` (Dao后缀)
- Repository接口: `PlanRepository` (Repository后缀)
- RepositoryImpl: `PlanRepositoryImpl` (Impl后缀)
- UseCase类: `CreatePlanUseCase`, `GetTodoItemsUseCase` (UseCase后缀)
- ViewModel: `HomeViewModel`, `AddPlanViewModel` (ViewModel后缀)
- Screen函数: `HomeScreen`, `AddPlanScreen` (Screen后缀)
- Component函数: `TodoItemCard`, `WeekCalendarView` (Card/View后缀)
- UiState: `HomeUiState`, `AddPlanUiState` (UiState后缀)
- UiEvent: `HomeUiEvent`, `AddPlanUiEvent` (UiEvent后缀)

### 代码风格
- 使用 `data class` 表示数据模型
- 使用 `sealed interface` 表示事件
- 使用 `Flow<T>` 表示响应式数据流
- 使用 `suspend` 函数表示异步操作
- 使用 `Result<T>` 表示操作结果

---

## 调试技巧

### 查看数据库内容
```kotlin
// 在ViewModel中添加调试代码
viewModelScope.launch {
    planRepository.getAllPlans().collect { plans ->
        Log.d("DEBUG", "Plans: $plans")
    }
}
```

### 查看状态变化
```kotlin
// 在Screen中
LaunchedEffect(uiState) {
    Log.d("DEBUG", "UiState: $uiState")
}
```

### 测试闹钟
在HomeViewModel中有 `debugAlarm()` 方法，可以在60秒后触发测试闹钟。

---

## 性能优化建议

1. 使用 `collectAsStateWithLifecycle()` 而不是 `collectAsState()`
2. 使用 `remember` 和 `LaunchedEffect` 避免不必要的重组
3. 使用 `Singleton` 作用域管理全局单例
4. 及时取消协程，使用 `viewModelScope`
5. 避免在UI层进行复杂计算

---

## 相关文档

- 详细架构分析: `PROJECT_STRUCTURE_ANALYSIS.md`
- 技术方案: `docs/01-技术方案.md`
- 数据层开发: `docs/03-数据层开发.md`
- 业务层开发: `docs/04-业务层开发.md`
- UI层开发: `docs/05-UI层开发.md`
- 系统集成开发: `docs/06-系统集成开发.md`
- 待办功能架构: `docs/07-待办功能架构方案.md`

