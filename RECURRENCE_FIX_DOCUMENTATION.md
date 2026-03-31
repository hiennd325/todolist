# Recurrence Fix - Full Technical Documentation

## 1. Mục tiêu
- Fix lỗi hiển thị recurring tasks trên Calendar: trước đây chỉ hiển thị 1 instance (theo createdAt), bây giờ hiển thị tất cả occurrences trong phạm vi xem.
- Hỗ trợ display mode toggle: Created date vs Deadline.
- Tạo persisted instances trong database.

## 2. Các thay đổi kỹ thuật

### 2.1 Database Migration (v4 → v5)
- Thêm 3 trường vào `todo_items`:
  * `recurrenceParentId INTEGER` (nullable, foreign key đến task gốc)
  * `occurrenceDate INTEGER` (nullable, thời gian cụ thể của instance)
  * `isRecurringInstance INTEGER NOT NULL DEFAULT 0`
- Tạo 2 index:
  * `idx_todo_items_recurrenceParentId`
  * `idx_todo_items_occurrenceDate`

### 2.2 New Files / Classes
- `CalendarDisplayMode.kt`: enum BY_CREATED, BY_DEADLINE
- `RecurrenceGenerator.kt`: tạo instances cho recurring tasks
  * Horizon: 30 ngày tương lai (có thể tùy chỉnh)
  * Hỗ trợ DAILY, WEEKLY, MONTHLY, YEARLY
  * Xử lý edge case: tháng 31, năm nhuận Feb 29
  * Copy reminderTime với giờ/phút giữ nguyên, chỉ thay đổi ngày

### 2.3 Cập nhật Existing Modules
- **SettingsManager**: Thêm `calendarDisplayMode` preference.
- **SettingsScreen**: Thêm Calendar section để toggle display mode.
- **TodoDao**: Thêm 5 queries mới cho recurrence.
- **TodoRepository**: Thêm hàm wrapper cho các query mới.
- **CalendarViewModel**: Hỗ trợ dual display mode, group by deadline cho instances.
- **TodoViewModel**: Tích hợp RecurrenceGenerator và NotificationScheduler, xử lý add/update/delete với recurrence.
- **MainActivity**: Inject RecurrenceGenerator và NotificationScheduler.
- **ExportImportManager**: Export/import các trường recurrence mới, bump version to 2.
- **BootReceiver**: Chỉ schedule single reminders (bỏ scheduleRecurringReminder).

### 2.4 Notification Scheduling
- Instances được schedule ngay sau khi tạo.
- Khi xóa parent, hủy reminders của tất cả instances.
- Khi update recurrence settings, regenerate và reschedule.

## 3. Kiểm thử

### 3.1 Unit Tests
- **RecurrenceGeneratorTest**:
  * DAILY tạo 30 instances.
  * WEEKLY tạo ~4 instances.
  * MONTHLY với ngày 31 tạo đúng tháng có 31, bỏ tháng 30/28.
  * YEARLY với Feb 29 tạo Feb 28 ở năm không nhuận.
  * Không duplicate nếu đã có instances.
  * deleteInstances xóa tất cả.
- **CalendarViewModelTest**:
  * groupTodosByDate với BY_CREATED dùng createdAt.
  * groupTodosByDate với BY_DEADLINE dùng occurrenceDate cho instances, deadline cho non-recurring, fallback.createdAt.
  * selectDate toggle.
  * goToToday, nextMonth, previousMonth.

### 3.2 UI Tests (Chưa viết, để sau)
- CalendarGrid hiển thị dot cho mỗi ngày có tasks (bao gồm instances).
- Click date, List hiển thị đúng số tasks.
- Toggle display mode: tasks chuyển sang ngày mới.

### 3.3 Manual Test Cases

| TC | Mô tả | Bước | Expected |
|----|-------|------|----------|
| TC1 | Tạo Daily recurring task với deadline hôm nay | 1. Add task "Daily Exercise", recurrence=DAILY, deadline=today 23:59<br>2. Mở Calendar, xem tháng hiện tại và 30 ngày tới | Task xuất hiện trên **30 ngày liên tiếp** (including today). Mỗi ngày có 1 instance. |
| TC2 | Tạo Weekly recurring task vào thứ 2 | 1. Add task "Monday Meeting", recurrence=WEEKLY, deadline=next Monday<br>2. Xem Calendar 4 tuần tới | Task chỉ xuất hiện vào các thứ 2. Số instances ≈ 4-5 tùy horizon. |
| TC3 | Monthly recurring vào ngày 31 | 1. Add task "Pay rent", recurrence=MONTHLY, deadline=Jan 31<br>2. Xem Calendar Jan→Mar | Jan 31: instance. Feb: **không có** (vì Feb ≤ 29). Mar 31: instance. |
| TC4 | Yearly recurring vào Feb 29 | 1. Add task "Birthday", recurrence=YEARLY, deadline=Feb 29 2020<br>2. Xem Calendar 2020-2022 | 2020-02-29, 2021-02-28, 2022-02-28 (hoặc 01/03 tùy Calendar). |
| TC5 | Toggle display mode | 1. Tạo task với createdAt=Mar 1, deadline=Mar 15, non-recurring<br>2. Mặc định BY_CREATED → task hiển thị trên Mar 1<br>3. Vào Settings → chọn BY_DEADLINE → task hiển thị trên Mar 15 | Task chuyển giữa hai ngày. |
| TC6 | Recurring task với reminder | 1. Add daily task với reminder 8:00 AM<br>2. Kiểm tra notification: mỗi instance phải có reminder đúng 8:00 AM ngày đó | Reminder được schedule cho từng instance. |
| TC7 | Complete một instance | 1. Chọn một instance (recurring) trên Calendar, đánh dấu complete<br>2. Parent task và các instance khác không bị ảnh hưởng | Chỉ instance đó là completed. |
| TC8 | Edit parent task (thay đổi recurrence) | 1. Tạo daily task<br>2. Sau đó edit thành WEEKLY<br>3. Kiểm tra DB: số lượng instances phải được xóa và tạo lại với tần suất mới | Instances cũ bị xóa, instances mới có frequency mới. |
| TC9 | Delete parent task | 1. Xóa recurring task từ TodoList<br>2. Kiểm tra DB: tất cả instances của nó cũng bị xóa | CASCADE delete. |
| TC10 | Import/Export với recurring tasks | 1. Export data có recurring tasks<br>2. Import vào thiết bị khác<br>3. Kiểm tra: parent và instances đều được giữ nguyên, relationships, reminderTimes | Data import đầy đủ. |

## 4. Log & Dump để chẩn đoán

Trong `generateInstances`, thêm log:
```kotlin
Log.d("RecurrenceGenerator", "Generated ${occurrences.size} instances for parent ${parentTask.id} from $startDate to $horizon")
```

Trong `CalendarViewModel.groupTodosByDate`, log:
```kotlin
Log.d("Calendar", "Display mode: $mode, total todos=${todos.size}, grouped=${todosByDate.size}")
```

Kiểm tra Logcat filter "Recurrence" hoặc "Calendar".

## 5. Data dump mẫu

**Parent Task:**
```json
{
  "id": 15,
  "title": "Exercise",
  "recurrenceType": "DAILY",
  "deadline": 1711843200000,
  "occurrenceDate": null,
  "isRecurringInstance": false,
  "recurrenceParentId": null
}
```

**Instances (30 ngày):**
```json
[
  {"id":151, "title":"Exercise", "recurrenceType":"DAILY", "deadline":1711929600000, "occurrenceDate":1711929600000, "isRecurringInstance":true, "recurrenceParentId":15},
  ...
]
```

## 6. Known Limitations & Future Work

- Chỉ support interval = 1 (không có "every 2 weeks").
- Không có exceptions/exclude dates.
- Horizon cố định 30 ngày. Có thể mở rộng thành "all future" hoặc configurable.
- Không có option để hiển thị parent task trên Calendar (có thể ẩn nếu muốn).
- Reminder của parent vẫn được schedule nếu có, nhưng thường không cần.

## 7. Rollback Plan

Nếu cần rollback:
- Giảm database version về 4, xóa các cột mới.
- Xóa RecurrenceGenerator.
- Revert CalendarViewModel.groupTodosByDate về dùng createdAt chỉ.
- Không dùng instances.

---

**End of documentation.**