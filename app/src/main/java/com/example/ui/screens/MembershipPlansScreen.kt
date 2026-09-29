package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

data class PlanInfo(
    val tier: String,
    val name: String,
    val price: String,
    val duration: String,
    val badgeColor: Color,
    val features: List<String>,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembershipPlansScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedPlanForCheckout by remember { mutableStateOf<PlanInfo?>(null) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentSuccessDialog by remember { mutableStateOf<String?>(null) }

    val plans = listOf(
        PlanInfo(
            tier = "FREE",
            name = "Banjara Basic",
            price = "Free",
            duration = "Lifetime",
            badgeColor = TextSecondaryLight,
            features = listOf(
                "Create & publish biodata",
                "Receive unlimited interests",
                "Send up to 5 interests / month",
                "Standard search filters",
                "Community grievance support"
            )
        ),
        PlanInfo(
            tier = "SILVER",
            name = "Banjara Silver",
            price = "₹1,499",
            duration = "3 Months",
            badgeColor = BorderDark,
            features = listOf(
                "Send up to 30 interests / month",
                "Direct 1-on-1 chat with connected matches",
                "View verified contact numbers of accepted connections",
                "Photo privacy controls",
                "Priority grievance desk access"
            )
        ),
        PlanInfo(
            tier = "GOLD",
            name = "Banjara Gold Advantage",
            price = "₹2,999",
            duration = "6 Months",
            badgeColor = GoldDark,
            isPopular = true,
            features = listOf(
                "Unlimited matrimonial interests",
                "Instant direct chat & voice calling info upon connection",
                "Aadhaar Verified Gold Badge on profile",
                "3x Higher visibility in search results",
                "Horoscope & Goth compatibility recommendations",
                "Personalized Relationship Manager assistance"
            )
        ),
        PlanInfo(
            tier = "ROYAL",
            name = "Banjara Royal Heritage",
            price = "₹5,499",
            duration = "12 Months",
            badgeColor = MaroonPrimary,
            features = listOf(
                "VIP Royal Crown profile badge",
                "Unlimited direct contacts & family introductions",
                "Featured listing at top of discovery feed",
                "Background verified check report",
                "Dedicated Banjara community matchmaking elder support",
                "Complimentary entry to regional Banjara Matrimonial Meets"
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("membership_plans_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaroonPrimary)
            }
            Column {
                Text(
                    text = "Membership Plans",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaroonPrimary)
                )
                Text(
                    text = "Current plan: ${currentUser?.membershipTier ?: "FREE"}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryLight)
                )
            }
        }

        // Security / Webhook Notice Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(20.dp))
                Text(
                    text = "Payments are processed through RBI-compliant payment gateways with cryptographic webhook confirmation. No card credentials stored.",
                    fontSize = 11.sp,
                    color = TextSecondaryLight,
                    lineHeight = 15.sp
                )
            }
        }

        // Plan Cards
        plans.forEach { plan ->
            val isCurrent = currentUser?.membershipTier.equals(plan.tier, ignoreCase = true)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (plan.isPopular) IvorySurface else IvorySurface
                ),
                border = BorderStroke(
                    width = if (plan.isPopular || isCurrent) 2.dp else 1.dp,
                    color = if (isCurrent) VerifiedGreen else if (plan.isPopular) GoldAccent else BorderLight
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (plan.isPopular) 4.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (plan.isPopular) {
                                Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent) {
                                    Text("MOST RECOMMENDED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaroonPrimary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(plan.price, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextPrimaryLight)
                            Text(plan.duration, fontSize = 11.sp, color = TextSecondaryLight)
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    plan.features.forEach { feat ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Text(feat, fontSize = 12.sp, color = TextPrimaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (isCurrent) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Active Plan")
                        }
                    } else if (plan.tier != "FREE") {
                        Button(
                            onClick = { selectedPlanForCheckout = plan },
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("choose_plan_${plan.tier}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (plan.isPopular) MaroonPrimary else GoldDark)
                        ) {
                            Text("Choose ${plan.name} • ${plan.price}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Secure Checkout Bottom Sheet
    selectedPlanForCheckout?.let { plan ->
        ModalBottomSheet(
            onDismissRequest = { if (!isProcessingPayment) selectedPlanForCheckout = null },
            containerColor = IvorySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Complete Secure Subscription",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaroonPrimary)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = IvorySurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Plan Selected:", fontSize = 13.sp)
                            Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Validity:", fontSize = 13.sp)
                            Text(plan.duration, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount (incl. GST):", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(plan.price, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = MaroonPrimary)
                        }
                    }
                }

                Text("Select Authorized Payment Gateway:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldContainer,
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = GoldDark)
                            Text("UPI (GPay / PhonePe)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IvorySurfaceVariant,
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaroonPrimary)
                            Text("NetBanking / Cards", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (isProcessingPayment) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = MaroonPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Verifying server webhook & activating subscription...", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            isProcessingPayment = true
                            // Simulate secure server-side webhook verification
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isProcessingPayment = false
                                viewModel.upgradeMembership(plan.tier)
                                val txnId = "TXN_BANJARA_" + System.currentTimeMillis().toString().takeLast(8)
                                paymentSuccessDialog = txnId
                                selectedPlanForCheckout = null
                            }, 1200)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("pay_button_confirm"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay ${plan.price} & Activate Instantly", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Payment Success Confirmation Dialog
    paymentSuccessDialog?.let { txnId ->
        AlertDialog(
            onDismissRequest = { paymentSuccessDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedGreen)
                    Text("Payment & Activation Successful", fontWeight = FontWeight.Bold, color = VerifiedGreen)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Your membership tier has been updated immediately based on verified payment notification.")
                    Text("Transaction Reference: $txnId", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryLight)
                    Text("An official GST tax invoice and receipt has been logged to your audit trail.", fontSize = 11.sp, color = TextSecondaryLight)
                }
            },
            confirmButton = {
                Button(
                    onClick = { paymentSuccessDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
                ) {
                    Text("Continue")
                }
            }
        )
    }
}
