package dev.roshana.presentation.uiComponent.dialogs

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dev.roshana.domain.R
import dev.roshana.domain.models.dialogs.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** PariSa;
coding and smoking ;)
 **/

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ShowDialog(type: DialogType, onDismiss: () -> Unit) {
    val item = remember { item }

    when (type) {
        DialogType.SIMPLE ->
            AlertDialog(
                text = {
                    Text(item.subtitle)
                },
                backgroundColor = Color.White,
                confirmButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Ok")
                    }
                },
                onDismissRequest = onDismiss
            )
        DialogType.TITLE ->
            AlertDialog(
                title = { Text(text = item.title, style = typography.h6) },
                text = {
                    Text(item.subtitle)
                },
                confirmButton = {
                    Row(modifier = Modifier) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(text = "Cancel", color = Color.Gray)
                        }
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(text = "Ok")
                        }
                    }
                },
                onDismissRequest = onDismiss
            )
        DialogType.VERTICALBUTTON ->
            AlertDialog(
                title = { Text(text = item.title, style = typography.h6) },
                text = {
                    Text(item.subtitle)
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(100.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(100.dp)
                    ) {
                        Text(text = "Ok")
                    }
                },
                onDismissRequest = onDismiss
            )
        DialogType.IMAGE ->
            AlertDialog(
                title = { Text(text = item.title, style = typography.h6) },
                text = {
                    Text(item.subtitle, modifier = Modifier.padding(bottom = 8.dp))
                    Image(
                        painter = painterResource(item.imageId),
                        contentDescription = null
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Ok")
                    }
                },
                onDismissRequest = onDismiss
            )
        DialogType.LONGDIALOG ->
            AlertDialog(
                title = { Text(text = item.title, style = typography.h6) },
                text = {
                    Column {
                        Text(item.subtitle, modifier = Modifier.padding(bottom = 8.dp))
                        Image(
                            painter = painterResource(item.imageId),
                            contentDescription = null
                        )
                        Text(
                            item.subtitle + item.title + item.subtitle + item.title,
                            style = typography.subtitle2
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Ok")
                    }
                },
                onDismissRequest = onDismiss,
            )
        DialogType.ROUNDED ->
            AlertDialog(
                title = { Text(text = item.title, style = typography.h6) },
                text = {
                    Column {
                        Text(item.subtitle, modifier = Modifier.padding(bottom = 8.dp))
                        Image(
                            painter = painterResource(item.imageId),
                            contentDescription = null,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Ok")
                    }
                },
                onDismissRequest = onDismiss,
                shape = RoundedCornerShape(16.dp)
            )
        DialogType.DATEPICKER -> {
            val context = LocalContext.current
            (context as? FragmentActivity)?.supportFragmentManager?.let { manager ->

                val builder = MaterialDatePicker.Builder.datePicker()
                    .build()

                builder.addOnPositiveButtonClickListener { selectedDate ->

                }
                builder.addOnDismissListener {
                    onDismiss()
                }
                builder.show(manager, "DatePicker")
            }
        }

        DialogType.TIMEPICKER -> {
            val context = LocalContext.current
            (context as? FragmentActivity)?.supportFragmentManager?.let { manager ->
                val builder = MaterialTimePicker.Builder()
                    .build()
                builder.addOnPositiveButtonClickListener {

                }
                builder.addOnDismissListener {
                    onDismiss.invoke()
                }
                builder.show(manager, "TimePicker")
            }
        }

    }
}

val item = Item(
    1,
    "Awesome List Item",
    "Very awesome list item has very awesome subtitle. This is bit long",
    R.drawable.food6
)
