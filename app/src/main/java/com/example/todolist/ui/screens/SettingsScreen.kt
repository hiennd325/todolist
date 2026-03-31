package com.example.todolist.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
 import androidx.compose.material.icons.filled.Add
 import androidx.compose.material.icons.filled.CalendarToday
 import androidx.compose.material.icons.filled.ColorLens
 import androidx.compose.material.icons.filled.Delete
 import androidx.compose.material.icons.filled.Download
 import androidx.compose.material.icons.filled.Edit
 import androidx.compose.material.icons.filled.Palette
 import androidx.compose.material.icons.filled.RestartAlt
 import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
 import com.example.todolist.data.export.ExportImportManager
 import com.example.todolist.data.model.CalendarDisplayMode
 import com.example.todolist.data.model.CustomCategory
import com.example.todolist.data.repository.CustomCategoryRepository
import com.example.todolist.data.settings.SettingsManager
import com.example.todolist.ui.theme.ThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val ThemePresets = listOf(
    ThemePreset.Default,
    ThemePreset.Ocean,
    ThemePreset.Sunset,
    ThemePreset.Forest,
    ThemePreset.Berry,
    ThemePreset.Lavender
)

private val FontSizeOptions = listOf("small", "medium", "large", "extra_large")
private val FontSizeLabels = listOf("Small", "Medium", "Large", "Extra Large")
private val DarkModeOptions = listOf("system", "light", "dark")
private val DarkModeLabels = listOf("System", "Light", "Dark")

private val PresetColors = mapOf(
    "Default" to Color(0xFF6750A4),
    "Ocean" to Color(0xFF0D47A1),
    "Sunset" to Color(0xFFFF6D00),
    "Forest" to Color(0xFF2E7D32),
    "Berry" to Color(0xFF8E24AA),
    "Lavender" to Color(0xFF673AB7)
)

private val DefaultPrimaryColor = Color(0xFF6750A4)

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val customCategoryRepository: CustomCategoryRepository,
    private val exportImportManager: ExportImportManager? = null
) : ViewModel() {

    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsManager.Settings(
            themePreset = ThemePreset.Default,
            darkMode = "system",
            dynamicColor = false,
            fontSize = "medium"
        )
    )

    val categories: StateFlow<List<CustomCategory>> = customCategoryRepository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateThemePreset(preset: ThemePreset) {
        viewModelScope.launch {
            settingsManager.updateThemePreset(preset)
        }
    }

    fun updateDarkMode(mode: String) {
        viewModelScope.launch {
            settingsManager.updateDarkMode(mode)
        }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.updateDynamicColor(enabled)
        }
    }

     fun updateFontSize(size: String) {
         viewModelScope.launch {
             settingsManager.updateFontSize(size)
         }
     }

     fun updateCalendarDisplayMode(mode: CalendarDisplayMode) {
         viewModelScope.launch {
             settingsManager.updateCalendarDisplayMode(mode)
         }
     }

     fun updateCustomColors(primaryColor: String?, primaryDarkColor: String?) {
        viewModelScope.launch {
            settingsManager.updateCustomColors(primaryColor, primaryDarkColor)
        }
    }

    fun resetCustomColors() {
        viewModelScope.launch {
            settingsManager.updateCustomColors(null, null)
        }
    }

    fun addCategory(name: String, color: String, iconName: String) {
        viewModelScope.launch {
            customCategoryRepository.insert(
                CustomCategory(name = name, color = color, iconName = iconName)
            )
        }
    }

    fun updateCategory(category: CustomCategory) {
        viewModelScope.launch {
            customCategoryRepository.update(category)
        }
    }

    fun deleteCategory(category: CustomCategory) {
        viewModelScope.launch {
            customCategoryRepository.delete(category)
        }
    }

    suspend fun exportData(uri: android.net.Uri): Boolean {
        return exportImportManager?.exportToJson(uri) ?: false
    }

    suspend fun importData(uri: android.net.Uri): Boolean {
        return exportImportManager?.importFromJson(uri) ?: false
    }

    class Factory(
        private val settingsManager: SettingsManager,
        private val customCategoryRepository: CustomCategoryRepository,
        private val exportImportManager: ExportImportManager? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsManager, customCategoryRepository, exportImportManager) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val settings by viewModel.settings.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showColorPicker by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CustomCategory?>(null) }
    var deletingCategory by remember { mutableStateOf<CustomCategory?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = viewModel.exportData(it)
                snackbarHostState.showSnackbar(
                    if (success) "Export successful!" else "Export failed"
                )
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = viewModel.importData(it)
                snackbarHostState.showSnackbar(
                    if (success) "Import successful!" else "Import failed"
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppearanceSection(
                    currentPreset = settings.themePreset,
                    currentDarkMode = settings.darkMode,
                    dynamicColorEnabled = settings.dynamicColor,
                    currentFontSize = settings.fontSize,
                    onPresetSelected = viewModel::updateThemePreset,
                    onDarkModeSelected = viewModel::updateDarkMode,
                    onDynamicColorChanged = viewModel::updateDynamicColor,
                    onFontSizeChanged = viewModel::updateFontSize
                )
            }

            item {
                CustomColorsSection(
                    customPrimaryColor = settings.customPrimaryColor,
                    onResetColors = viewModel::resetCustomColors,
                    onPickColor = { showColorPicker = true }
                )
            }

             item {
                 CategoriesSection(
                     categories = categories,
                     onAddCategory = { showAddCategoryDialog = true },
                     onEditCategory = { editingCategory = it },
                     onDeleteCategory = { deletingCategory = it }
                 )
             }

             item {
                 CalendarSection(
                     currentDisplayMode = settings.calendarDisplayMode,
                     onModeSelected = viewModel::updateCalendarDisplayMode
                 )
             }

             item {
                 DataSection(
                     onExport = {
                         exportLauncher.launch("todolist_backup_${System.currentTimeMillis()}.json")
                     },
                     onImport = {
                         importLauncher.launch(arrayOf("application/json"))
                     }
                 )
             }
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = settings.customPrimaryColor?.let {
                try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { DefaultPrimaryColor }
            } ?: DefaultPrimaryColor,
            onColorSelected = { color ->
                val hexColor = String.format("#%06X", 0xFFFFFF and color.toArgb())
                viewModel.updateCustomColors(hexColor, hexColor)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }

    if (showAddCategoryDialog) {
        CategoryDialog(
            title = "Add Category",
            initialName = "",
            initialColor = "#00897B",
            onConfirm = { name, color ->
                viewModel.addCategory(name, color, "category")
                showAddCategoryDialog = false
            },
            onDismiss = { showAddCategoryDialog = false }
        )
    }

    editingCategory?.let { category ->
        CategoryDialog(
            title = "Edit Category",
            initialName = category.name,
            initialColor = category.color,
            onConfirm = { name, color ->
                viewModel.updateCategory(category.copy(name = name, color = color))
                editingCategory = null
            },
            onDismiss = { editingCategory = null }
        )
    }

    deletingCategory?.let { category ->
        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete \"${category.name}\"? Tasks in this category will not be deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(category)
                    deletingCategory = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AppearanceSection(
    currentPreset: ThemePreset,
    currentDarkMode: String,
    dynamicColorEnabled: Boolean,
    currentFontSize: String,
    onPresetSelected: (ThemePreset) -> Unit,
    onDarkModeSelected: (String) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onFontSizeChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Theme Preset",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ThemePresets.forEach { preset ->
                    val presetName = ThemePreset.toString(preset)
                    val presetColor = PresetColors[presetName] ?: DefaultPrimaryColor

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onPresetSelected(preset) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentPreset == preset,
                            onClick = { onPresetSelected(preset) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(presetColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = presetName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DarkModeOptions.forEachIndexed { index, mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDarkModeSelected(mode) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentDarkMode == mode,
                            onClick = { onDarkModeSelected(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = DarkModeLabels[index],
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dynamic Color",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Use wallpaper-based colors (Android 12+)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = dynamicColorEnabled,
                    onCheckedChange = onDynamicColorChanged
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Font Size",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val currentFontSizeIndex = FontSizeOptions.indexOf(currentFontSize).coerceAtLeast(1)
            var sliderPosition by remember(currentFontSizeIndex) {
                mutableFloatStateOf(currentFontSizeIndex.toFloat())
            }

            Column {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        val index = sliderPosition.toInt().coerceIn(0, FontSizeOptions.size - 1)
                        onFontSizeChanged(FontSizeOptions[index])
                    },
                    valueRange = 0f..(FontSizeOptions.size - 1).toFloat(),
                    steps = FontSizeOptions.size - 2
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FontSizeLabels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomColorsSection(
    customPrimaryColor: String?,
    onResetColors: () -> Unit,
    onPickColor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Custom Colors",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Primary Color",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val displayColor = customPrimaryColor?.let {
                        try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { DefaultPrimaryColor }
                    } ?: DefaultPrimaryColor

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(displayColor)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { onPickColor() }
                    )
                }

                if (customPrimaryColor != null) {
                    OutlinedButton(onClick = onResetColors) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }

            if (customPrimaryColor != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Custom color: $customPrimaryColor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<CustomCategory>,
    onAddCategory: () -> Unit,
    onEditCategory: (CustomCategory) -> Unit,
    onDeleteCategory: (CustomCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onAddCategory) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add category"
                    )
                }
            }

            if (categories.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No custom categories yet. Tap + to add one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                categories.forEach { category ->
                    CategoryItem(
                        category = category,
                        onEdit = { onEditCategory(category) },
                        onDelete = { onDeleteCategory(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CustomCategory,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (_: Exception) {
        Color(0xFF00897B)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(categoryColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onEdit,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
     }
 }

 @Composable
 private fun CalendarSection(
     currentDisplayMode: CalendarDisplayMode,
     onModeSelected: (CalendarDisplayMode) -> Unit
 ) {
     Card(
         modifier = Modifier.fillMaxWidth(),
         shape = RoundedCornerShape(12.dp),
         colors = CardDefaults.cardColors(
             containerColor = MaterialTheme.colorScheme.surface
         )
     ) {
         Column(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp)
         ) {
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                 Icon(
                     imageVector = Icons.Default.CalendarToday,
                     contentDescription = null,
                     tint = MaterialTheme.colorScheme.primary
                 )
                 Text(
                     text = "Calendar Display",
                     style = MaterialTheme.typography.titleMedium,
                     fontWeight = FontWeight.Bold
                 )
             }

             Spacer(modifier = Modifier.height(16.dp))

             Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                 CalendarDisplayMode.entries.forEach { mode ->
                     val label = when (mode) {
                         CalendarDisplayMode.BY_CREATED -> "Created date"
                         CalendarDisplayMode.BY_DEADLINE -> "Deadline"
                     }

                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .clip(RoundedCornerShape(8.dp))
                             .clickable { onModeSelected(mode) }
                             .padding(vertical = 8.dp, horizontal = 4.dp),
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         RadioButton(
                             selected = currentDisplayMode == mode,
                             onClick = { onModeSelected(mode) }
                         )
                         Spacer(modifier = Modifier.width(8.dp))
                         Text(
                             text = label,
                             style = MaterialTheme.typography.bodyMedium
                         )
                     }
                 }
             }
         }
     }
 }

 @Composable
 private fun DataSection(
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onExport,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export")
                }

                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Import")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Export your data as JSON for backup or transfer to another device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ColorPickerDialog(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val presetColorList = listOf(
        Color(0xFF6750A4), Color(0xFF0D47A1), Color(0xFF0288D1),
        Color(0xFF00897B), Color(0xFF2E7D32), Color(0xFF558B2F),
        Color(0xFFFF6D00), Color(0xFFFF9800), Color(0xFFD81B60),
        Color(0xFF8E24AA), Color(0xFF673AB7), Color(0xFF5C6BC0),
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF795548),
        Color(0xFF607D8B)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Primary Color") },
        text = {
            Column {
                Text(
                    text = "Choose a color:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                val rows = presetColorList.chunked(4)
                rows.forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowColors.forEach { color ->
                            val isSelected = color == currentColor
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface
                                            else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable { onColorSelected(color) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CategoryDialog(
    title: String,
    initialName: String,
    initialColor: String,
    onConfirm: (name: String, color: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var color by remember { mutableStateOf(initialColor) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val categoryColors = listOf(
        "#2196F3", "#03A9F4", "#00BCD4", "#009688",
        "#4CAF50", "#8BC34A", "#CDDC39", "#FF9800",
        "#FF5722", "#F44336", "#E91E63", "#9C27B0",
        "#673AB7", "#3F51B5", "#795548", "#607D8B"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Category Name") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                val rows = categoryColors.chunked(4)
                rows.forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowColors.forEach { colorHex ->
                            val colorValue = try {
                                Color(android.graphics.Color.parseColor(colorHex))
                            } catch (_: Exception) {
                                Color.Gray
                            }
                            val isSelected = color == colorHex
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(colorValue)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface
                                            else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable { color = colorHex }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                    } else {
                        onConfirm(name.trim(), color)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
