package com.example.todolist.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTodoItemCard(
    todo: TodoItem,
    onToggleComplete: () -> Unit,
    onToggleStarred: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onShowUndoSnackbar: ((() -> Unit) -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    var hasTriggeredHaptic by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    if (onShowUndoSnackbar != null) {
                        onShowUndoSnackbar(onDelete)
                    } else {
                        onDelete()
                    }
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggleComplete()
                    true
                }
                SwipeToDismissBoxValue.Settled -> {
                    hasTriggeredHaptic = false
                    false
                }
            }
        },
        positionalThreshold = { it * 0.5f }
    )

    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue != SwipeToDismissBoxValue.Settled && !hasTriggeredHaptic) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasTriggeredHaptic = true
        }
    }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.Settled) {
            hasTriggeredHaptic = false
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp)),
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50).copy(alpha = 0.9f)
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336).copy(alpha = 0.9f)
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "backgroundColor"
            )

            val iconScale by animateFloatAsState(
                targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) 1.3f else 0f,
                label = "iconScale"
            )

            val progress = dismissState.progress
            val iconAlpha by animateFloatAsState(
                targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) 1f else 0f,
                label = "iconAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Arrangement.Start
                        SwipeToDismissBoxValue.EndToStart -> Arrangement.End
                        SwipeToDismissBoxValue.Settled -> Arrangement.Center
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .scale(iconScale)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "COMPLETE",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        SwipeToDismissBoxValue.EndToStart -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .scale(iconScale)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "DELETE",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        SwipeToDismissBoxValue.Settled -> {
                            // Nothing when settled
                        }
                    }
                }
            }
        },
        enableDismissFromStartToEnd = !todo.isCompleted,
        enableDismissFromEndToStart = true
    ) {
        TodoItemCard(
            todo = todo,
            onToggleComplete = onToggleComplete,
            onToggleStarred = onToggleStarred,
            onEdit = onEdit,
            onDelete = onDelete,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
