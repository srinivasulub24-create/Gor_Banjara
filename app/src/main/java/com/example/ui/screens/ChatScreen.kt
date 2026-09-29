package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MessageEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ReportProfileDialog
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    partner: UserProfileEntity,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val messagesFlow = remember(partner.id) { viewModel.getChatMessages(partner.id) }
    val messages by messagesFlow.collectAsState(initial = emptyList())
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val icebreakers = listOf(
        "Namaste! Delighted to connect with your family.",
        "Could you tell me more about your work and hobbies?",
        "When is a good time for our parents to speak over a call?",
        "Do you celebrate Sevalal Maharaj Jayanti in your native Tanda?"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = partner.photoUrl,
                            contentDescription = null,
                            modifier = Modifier.size(38.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = partner.fullName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "Goth: ${partner.clan} • Connected",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryLight)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.ReportProblem, contentDescription = "Report conversation", tint = MaroonPrimary)
                    }
                    IconButton(onClick = { showBlockConfirmDialog = true }) {
                        Icon(Icons.Default.Block, contentDescription = "Block user", tint = SafetyRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IvorySurface)
            )
        },
        bottomBar = {
            Surface(
                color = IvorySurface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Icebreakers horizontal row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icebreakers.forEach { phrase ->
                            SuggestionChip(
                                onClick = { inputText = phrase },
                                label = { Text(phrase, fontSize = 11.sp, maxLines = 1) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = IvorySurfaceVariant),
                                border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = BorderLight)
                            )
                        }
                    }

                    // Input & Send
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Type a respectful matrimonial message...", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp, max = 100.dp)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaroonPrimary,
                                unfocusedBorderColor = BorderLight,
                                focusedContainerColor = IvorySurface,
                                unfocusedContainerColor = IvorySurface
                            )
                        )

                        FilledIconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(partner.id, inputText)
                                    inputText = ""
                                    coroutineScope.launch {
                                        if (messages.isNotEmpty()) {
                                            listState.animateScrollToItem(messages.size - 1)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(46.dp).testTag("chat_send_button"),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaroonPrimary),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Matrimonial Safety Banner
            Surface(
                color = IvorySurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Safety Notice: Never share financial passwords, OTPs, or transfer money. Involve elders for family meetings.",
                        fontSize = 11.sp,
                        color = TextSecondaryLight,
                        lineHeight = 14.sp
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == "user_me"
                    ChatBubble(message = msg, isOutgoing = isMe)
                }
            }
        }
    }

    if (showReportDialog) {
        ReportProfileDialog(
            targetName = partner.fullName,
            onDismiss = { showReportDialog = false },
            onSubmitReport = { cat, det ->
                viewModel.reportProfile(partner.id, partner.fullName, cat, det)
                showReportDialog = false
            }
        )
    }

    if (showBlockConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlockConfirmDialog = false },
            title = { Text("Block ${partner.fullName}?", fontWeight = FontWeight.Bold, color = MaroonPrimary) },
            text = { Text("You will no longer be able to message each other, and connections will be severed.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.blockUser(partner.id, partner.fullName)
                        showBlockConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyRed)
                ) {
                    Text("Block")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ChatBubble(message: MessageEntity, isOutgoing: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isOutgoing) 14.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 14.dp
            ),
            color = if (isOutgoing) MaroonPrimary else IvorySurface,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.content,
                    color = if (isOutgoing) Color.White else TextPrimaryLight,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Delivered",
                        fontSize = 9.sp,
                        color = if (isOutgoing) GoldContainer else TextSecondaryLight
                    )
                    if (isOutgoing) {
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
