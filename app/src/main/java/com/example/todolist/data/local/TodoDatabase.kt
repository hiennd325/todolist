package com.example.todolist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.data.model.CustomCategory
import com.example.todolist.data.model.Subtask
import com.example.todolist.data.model.TaskList
import com.example.todolist.data.model.TodoItem

@Database(
    entities = [TodoItem::class, TaskList::class, Subtask::class, CustomCategory::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun taskListDao(): TaskListDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun customCategoryDao(): CustomCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create task_lists table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `task_lists` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT NOT NULL DEFAULT '#00897B',
                        `icon` TEXT NOT NULL DEFAULT 'list',
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // Insert default task list
                db.execSQL("""
                    INSERT INTO `task_lists` (`name`, `color`, `icon`, `createdAt`, `updatedAt`, `sortOrder`)
                    VALUES ('My Tasks', '#00897B', 'list', ${System.currentTimeMillis()}, ${System.currentTimeMillis()}, 0)
                """)
                
                // Create subtasks table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `subtasks` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `todoId` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(`todoId`) REFERENCES `todo_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subtasks_todoId` ON `subtasks` (`todoId`)")
                
                // Add new columns to todo_items
                db.execSQL("ALTER TABLE `todo_items` ADD COLUMN `isStarred` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `todo_items` ADD COLUMN `reminderTime` INTEGER")
                db.execSQL("ALTER TABLE `todo_items` ADD COLUMN `recurrenceType` TEXT NOT NULL DEFAULT 'NONE'")
                db.execSQL("ALTER TABLE `todo_items` ADD COLUMN `taskListId` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `todo_items` ADD COLUMN `sortOrder` INTEGER NOT NULL DEFAULT 0")
                
                // Create index for taskListId
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_taskListId` ON `todo_items` (`taskListId`)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `custom_categories` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT NOT NULL DEFAULT '#00897B',
                        `iconName` TEXT NOT NULL DEFAULT 'category',
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                
                val currentTime = System.currentTimeMillis()
                db.execSQL("INSERT INTO `custom_categories` (`name`, `color`, `iconName`, `createdAt`) VALUES ('Work', '#FF5722', 'work', $currentTime)")
                db.execSQL("INSERT INTO `custom_categories` (`name`, `color`, `iconName`, `createdAt`) VALUES ('Personal', '#4CAF50', 'person', $currentTime)")
                db.execSQL("INSERT INTO `custom_categories` (`name`, `color`, `iconName`, `createdAt`) VALUES ('Shopping', '#2196F3', 'shopping_cart', $currentTime)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `todo_items` ADD COLUMN `estimatedDurationMinutes` INTEGER")
            }
        }

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val callback = object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL(
                            "INSERT OR IGNORE INTO `task_lists` (`name`, `color`, `icon`, `createdAt`, `updatedAt`, `sortOrder`) " +
                            "VALUES ('My Tasks', '#00897B', 'list', ${System.currentTimeMillis()}, ${System.currentTimeMillis()}, 0)"
                        )
                        val currentTime = System.currentTimeMillis()
                        db.execSQL("INSERT OR IGNORE INTO `custom_categories` (`name`, `color`, `iconName`, `createdAt`) VALUES ('Work', '#FF5722', 'work', $currentTime)")
                        db.execSQL("INSERT OR IGNORE INTO `custom_categories` (`name`, `color`, `iconName`, `createdAt`) VALUES ('Personal', '#4CAF50', 'person', $currentTime)")
                        db.execSQL("INSERT OR IGNORE INTO `custom_categories` (`name`, `color`, `iconName`, `createdAt`) VALUES ('Shopping', '#2196F3', 'shopping_cart', $currentTime)")
                    }
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .addCallback(callback)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
