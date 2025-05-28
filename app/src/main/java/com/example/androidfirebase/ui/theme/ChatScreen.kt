package com.example.androidfirebase.ui.theme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidfirebase.Message
import com.example.androidfirebase.MessageViewModel
import com.example.androidfirebase.R

import com.example.androidfirebase.Room
import com.example.androidfirebase.utils.formatTimestamp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    roomId: String,
    roomName: String,
    onBackPressed: () -> Unit,
    messageViewModel: MessageViewModel = viewModel()
) {
    messageViewModel.setRoomId(roomId)
    val messages by messageViewModel.messages.observeAsState(emptyList())
    val text = remember { mutableStateOf("") }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            Text(
                text = roomName,
                style = TextStyle(fontSize = 20.sp, color = Color.Black),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {



            // Display the chat messages
            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message = message.copy(
                        isSentByCurrentUser = message.senderId == messageViewModel.currentUser.value?.email
                    ))
                }
            }

            // Chat input field and send icon
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )

                IconButton (
                    onClick = {
                        // Send the message when the icon is clicked
                        if (text.value.isNotEmpty()) {
                            messageViewModel.sendMessage(text.value.trim())
                            text.value = ""
                        }
                    }
                ){
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (message.isSentByCurrentUser) Color(0xFFBB86FC) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = message.senderFirstName,
                    color = if (message.isSentByCurrentUser) Color.White else Color.Black,
                    style = TextStyle(fontSize = 13.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Text(
            text = formatTimestamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}
