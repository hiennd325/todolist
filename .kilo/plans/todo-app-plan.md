# Plan: Todo List App với Room Database và Tính năng Nâng cao

## Tổng quan
Xây dựng ứng dụng Todo List hoàn chỉnh trên nền tảng project Android Kotlin Compose hiện tại, sử dụng Room Database để lưu trữ dữ liệu và các tính năng nâng cao: deadline, ưu tiên, tìm kiếm, lọc, sắp xếp.

## Kiến trúc
- **MVVM** (Model-View-ViewModel) với Jetpack Compose
- **Room Database** cho persistence
- **StateFlow/State** cho reactive UI

## Cấu trúc thư mục
```
app/src/main/java/com/example/todolist/
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── TodoDatabase.kt          # Room Database
│   │   ├── TodoDao.kt               # Data Access Object
│   │   └── Converters.kt            # Type converters (Date, Priority)
│   ├── model/
│   │   ├── TodoItem.kt              # Entity chính
│   │   ├── Priority.kt              # Enum: HIGH, MEDIUM, LOW
│   │   └── Category.kt              # Enum: WORK, PERSONAL, SHOPPING, HEALTH, OTHER
│   └── repository/
│       └── TodoRepository.kt        # Repository pattern
├── ui/
│   ├── theme/
│   │   ├── Color.kt                 # Mở rộng màu sắc
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── components/
│   │   ├── TodoItemCard.kt          # Card hiển thị 1 todo
│   │   ├── AddEditTodoDialog.kt     # Dialog thêm/sửa todo
│   │   ├── FilterChipGroup.kt       # Chips lọc theo category/priority
│   │   ├── PriorityDropdown.kt      # Dropdown chọn priority
│   │   └── CategoryDropdown.kt      # Dropdown chọn category
│   ├── screens/
│   │   └── TodoListScreen.kt        # Màn hình chính
│   └── viewmodel/
│       └── TodoViewModel.kt         # ViewModel quản lý state
└── ui/theme/
```

## Chi tiết từng file

### 1. Data Layer

#### `data/model/Priority.kt`
- Enum: HIGH, MEDIUM, LOW
- Mỗi priority có label tiếng Việt và màu sắc tương ứng

#### `data/model/Category.kt`
- Enum: WORK, PERSONAL, SHOPPING, HEALTH, OTHER
- Mỗi category có label tiếng Việt và icon Material

#### `data/model/TodoItem.kt`
- Room Entity với các field: id, title, description, isCompleted, priority, category, deadline (Long?), createdAt, updatedAt

#### `data/local/TodoDao.kt`
- getAllTodos(): Flow<List<TodoItem>>
- getTodosByCategory(category): Flow<List<TodoItem>>
- getTodosByPriority(priority): Flow<List<TodoItem>>
- searchTodos(query): Flow<List<TodoItem>>
- insert(todo), update(todo), delete(todo)
- getTodoById(id): Flow<TodoItem?>

#### `data/local/TodoDatabase.kt`
- RoomDatabase với 1 entity TodoItem
- Singleton pattern với @Database annotation

#### `data/local/Converters.kt`
- @TypeConverter cho Priority, Category enum và Long timestamp

#### `data/repository/TodoRepository.kt`
- Wrap TodoDao, cung cấp các Flow cho ViewModel

### 2. UI Layer

#### `ui/viewmodel/TodoViewModel.kt`
- Inject Repository
- State: todos list, search query, selected filter (category, priority), sort mode
- Actions: addTodo, updateTodo, deleteTodo, toggleComplete, setSearchQuery, setFilter, setSortMode

#### `ui/screens/TodoListScreen.kt`
- TopBar: Search bar + sort button
- Filter chips: All, theo Category, theo Priority
- LazyColumn hiển thị TodoItemCard
- FAB: Thêm mới todo
- Empty state khi không có todo

#### `ui/components/TodoItemCard.kt`
- Card với checkbox, title, description preview
- Priority indicator (màu viền/chấm)
- Category icon
- Deadline display (format: "dd/MM/yyyy" hoặc "Hết hạn"/"Còn X ngày")
- Swipe to delete
- Click để edit

#### `ui/components/AddEditTodoDialog.kt`
- TextField: Tiêu đề, Mô tả
- PriorityDropdown, CategoryDropdown
- DatePicker: chọn deadline
- Button: Lưu / Hủy

### 3. Dependencies cần thêm

#### libs.versions.toml
- room = "2.6.1"
- lifecycleViewModelCompose = "2.8.0"
- Thêm libraries: room-runtime, room-ktx, room-compiler, lifecycle-viewmodel-compose

#### app/build.gradle.kts
- Thêm kapt plugin
- Thêm room runtime, ktx, compiler dependencies
- Thêm lifecycle-viewmodel-compose dependency

### 4. Thứ tự thực hiện
1. Thêm dependencies vào `libs.versions.toml` và `app/build.gradle.kts`
2. Tạo data model (`Priority`, `Category`, `TodoItem`)
3. Tạo Room Database layer (`Converters`, `TodoDao`, `TodoDatabase`)
4. Tạo Repository (`TodoRepository`)
5. Tạo ViewModel (`TodoViewModel`)
6. Tạo UI Components (`TodoItemCard`, `AddEditTodoDialog`, `FilterChipGroup`, dropdowns)
7. Tạo Main Screen (`TodoListScreen`)
8. Cập nhật `MainActivity.kt`

## Tính năng hoàn chỉnh
- ✅ Thêm todo mới với tiêu đề, mô tả
- ✅ Sửa todo hiện có
- ✅ Xóa todo (swipe hoặc button)
- ✅ Đánh dấu hoàn thành/chưa hoàn thành
- ✅ Phân loại theo Category (Công việc, Cá nhân, Mua sắm, Sức khỏe, Khác)
- ✅ Phân loại theo Priority (Cao, Trung bình, Thấp)
- ✅ Đặt deadline cho todo
- ✅ Tìm kiếm theo tiêu đề/mô tả
- ✅ Lọc theo Category và Priority
- ✅ Sắp xếp theo: Thời gian tạo, Deadline, Priority
- ✅ Lưu trữ dữ liệu bằng Room Database (không mất khi đóng app)
