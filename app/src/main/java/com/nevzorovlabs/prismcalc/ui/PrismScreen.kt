package com.nevzorovlabs.prismcalc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nevzorovlabs.prismcalc.logic.EyeInput
import com.nevzorovlabs.prismcalc.logic.PrismCalculator
import com.nevzorovlabs.prismcalc.logic.PrismResult
import com.nevzorovlabs.prismcalc.ui.theme.AccentTeal
import com.nevzorovlabs.prismcalc.ui.theme.SurfaceVariantDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrismScreen() {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var rightUp by remember { mutableStateOf("0") }
    var rightDown by remember { mutableStateOf("0") }
    var rightOut by remember { mutableStateOf("0") }
    var rightIn by remember { mutableStateOf("0") }

    var leftUp by remember { mutableStateOf("0") }
    var leftDown by remember { mutableStateOf("0") }
    var leftOut by remember { mutableStateOf("0") }
    var leftIn by remember { mutableStateOf("0") }

    var result by remember { mutableStateOf<PrismResult?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resultant Prism Calculator",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ophthalmic prism summation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EyeColumn(
                    title = "Right (OD)",
                    up = rightUp, onUp = { rightUp = it },
                    down = rightDown, onDown = { rightDown = it },
                    outVal = rightOut, onOut = { rightOut = it },
                    inVal = rightIn, onIn = { rightIn = it },
                    modifier = Modifier.weight(1f)
                )
                EyeColumn(
                    title = "Left (OS)",
                    up = leftUp, onUp = { leftUp = it },
                    down = leftDown, onDown = { leftDown = it },
                    outVal = leftOut, onOut = { leftOut = it },
                    inVal = leftIn, onIn = { leftIn = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val right = EyeInput(
                        up = parseField(rightUp),
                        down = parseField(rightDown),
                        outVal = parseField(rightOut),
                        inVal = parseField(rightIn)
                    )
                    val left = EyeInput(
                        up = parseField(leftUp),
                        down = parseField(leftDown),
                        outVal = parseField(leftOut),
                        inVal = parseField(leftIn)
                    )

                    val allZero = listOf(
                        right.up, right.down, right.outVal, right.inVal,
                        left.up, left.down, left.outVal, left.inVal
                    ).all { it == 0.0 }

                    if (allZero) {
                        result = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Enter at least one prism value")
                        }
                    } else {
                        result = PrismCalculator.calculate(right, left)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(
                    text = "Calculate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = result != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                result?.let { ResultCard(result = it) }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EyeColumn(
    title: String,
    up: String, onUp: (String) -> Unit,
    down: String, onDown: (String) -> Unit,
    outVal: String, onOut: (String) -> Unit,
    inVal: String, onIn: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = AccentTeal,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            PrismField(
                value = up,
                onValueChange = onUp,
                label = "Up"
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrismField(
                    value = outVal,
                    onValueChange = onOut,
                    label = "Out",
                    modifier = Modifier.weight(1f)
                )
                EyeIcon()
                PrismField(
                    value = inVal,
                    onValueChange = onIn,
                    label = "In",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))

            PrismField(
                value = down,
                onValueChange = onDown,
                label = "Down"
            )
        }
    }
}

@Composable
private fun EyeIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(SurfaceVariantDark),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👁",
            fontSize = 20.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrismField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(sanitizeInput(input))
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentTeal,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = AccentTeal,
            cursorColor = AccentTeal
        )
    )
}

private fun sanitizeInput(raw: String): String {
    val trimmed = raw.replace(',', '.')
    val filtered = trimmed.filterIndexed { index, c ->
        c.isDigit() || c == '.' || (c == '-' && index == 0)
    }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) {
        filtered
    } else {
        filtered.substring(0, firstDot + 1) +
            filtered.substring(firstDot + 1).replace(".", "")
    }
}

private fun parseField(raw: String): Double {
    val cleaned = raw.replace(',', '.').trim()
    if (cleaned.isEmpty() || cleaned == "-" || cleaned == ".") return 0.0
    return cleaned.toDoubleOrNull() ?: 0.0
}
