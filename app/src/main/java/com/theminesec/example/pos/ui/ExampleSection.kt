package com.theminesec.example.pos.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.GsonBuilder
import com.theminesec.app.poslib.MsaPosApi
import com.theminesec.app.poslib.model.PosRequest
import com.theminesec.app.poslib.model.PosResponse
import com.theminesec.example.pos.ui.UiSection.Companion.getTitle
import com.theminesec.example.pos.ui.component.BrandedButton
import com.theminesec.example.pos.util.DemoApp
import com.theminesec.example.pos.util.transformActivationCode
import kotlinx.coroutines.launch

private enum class UiSection(val title: String) {
    CheckInstallation("Check SoftPOS Installation"),
    WarmUp("Warm Up Checking"),
    Activation("Activation App"),
    EnquiryApp("Enquiry App Status"),
    SaleTran("Sale Request"),
    EnquiryTran("Enquiry Transaction Status"),
    EnquiryTranWithMessageId("Enquiry Transaction Status With Pos MessageId"),
    VoidTran("Void Transaction"),
    Settlement("Settle Batch"),
    RefundTran("Refund (After Settlement)"),
    ReloadConfiguration("Reload Configuration"),
    EnquiryBluetoothConnectStatus("Enquiry Bluetooth Connect Status");

    companion object {
        fun UiSection.getTitle() = "${this.ordinal + 1}. ${this.title}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleSection(
    snackbarHostState: SnackbarHostState,
) {
    val prettyGson = remember { GsonBuilder().setPrettyPrinting().create() }
    val localFocusManager = LocalFocusManager.current
    val localContext = LocalContext.current
    val viewModel = viewModel(modelClass = ExampleViewModel::class.java)
    val scope = rememberCoroutineScope()
    val switchInteractSrc = remember { MutableInteractionSource() }
    var expanded by remember { mutableStateOf(false) }
    val dropdownOptions = remember { DemoApp.entries.map { it.name } }

    val msaPosApi = remember(viewModel.demoApp.name) {
        MsaPosApi(viewModel.demoApp.param.packageName)
    }

    var isSoftPosInstalled by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = msaPosApi) {
        isSoftPosInstalled = msaPosApi.isSoftPosInstalled(localContext)
    }

    val warmUpLauncher =
        rememberLauncherForActivityResult(msaPosApi.warmUpContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }
    val activationLauncher =
        rememberLauncherForActivityResult(msaPosApi.activationContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }
    val enquiryAppLauncher =
        rememberLauncherForActivityResult(msaPosApi.enquiryDeviceStatusContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }
    val transactionLauncher =
        rememberLauncherForActivityResult(msaPosApi.transactionContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
            if (it is PosResponse.Success) {
                viewModel.cachedTransactionId = it.data.tranId
            }
            viewModel.cachedPosMessageId = viewModel.posMessageId
            viewModel.resetRandomPosMessageId()
        }
    val enquiryTranLauncher =
        rememberLauncherForActivityResult(msaPosApi.enquiryTranStatusContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }
    val enquiryTranWithMessageIdLauncher =
        rememberLauncherForActivityResult(msaPosApi.enquiryTranStatusWithMessageIdContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }
    val settlementLauncher =
        rememberLauncherForActivityResult(msaPosApi.settlementContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
            viewModel.resetRandomPosMessageId()
        }
    val reloadConfigurationLauncher =
        rememberLauncherForActivityResult(msaPosApi.reloadConfigurationContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }

    val enquiryBTConnectStatusLauncher =
        rememberLauncherForActivityResult(msaPosApi.enquiryBTConnectStatusContract()) {
            viewModel.writeMessage(it::class.simpleName.orEmpty())
            viewModel.writeMessage(prettyGson.toJson(it))
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) { detectTapGestures(onTap = { localFocusManager.clearFocus() }) },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier)

        Text(text = UiSection.CheckInstallation.getTitle())
        Text(text = "Start from Android 11 (API level 30), the app require to declare <queries> in Manifest")
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = viewModel.demoApp.name,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            DropdownMenu(
                modifier = Modifier
                    .requiredSizeIn(maxHeight = 280.dp)
                    .exposedDropdownSize(),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, 4.dp),
            ) {
                dropdownOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            viewModel.changeDemoApp(option)
                            expanded = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Package Name") },
            value = viewModel.demoApp.param.packageName,
            onValueChange = {},
            enabled = false
        )

        BrandedButton(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "SoftPOS App: ${viewModel.demoApp.param.packageName}\n" +
                                "Installed? ${msaPosApi.isSoftPosInstalled(localContext)}"
                    )
                }
            }
        ) {
            Text(text = "Check SoftPOS app")
        }

        Divider()
        Text(text = UiSection.WarmUp.getTitle())
        Text(text = "The SoftPOS app would need to perform a runtime check before the API call. This could take few seconds to do so")
        BrandedButton(
            onClick = {
                viewModel.writeMessage("Launch - WarmUp")
                warmUpLauncher.launch()
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Warm Up")
        }

        Divider()
        Text(text = UiSection.Activation.getTitle())
        TextButton(
            onClick = { viewModel.restoreDefaultActivationCode() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Restore default activation code")
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Activation Code") },
            value = viewModel.activationCode,
            onValueChange = {
                viewModel.activationCode = it.filter(Char::isDigit).take(12)
            },
            isError = viewModel.activationCode.length < 12,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = { transformActivationCode(it) }
        )
        BrandedButton(
            onClick = {
                val activationReq = PosRequest.Activation(viewModel.activationCode)
                viewModel.writeMessage(activationReq.toString())
                activationLauncher.launch(activationReq)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Activation")
        }

        Divider()
        Text(text = UiSection.EnquiryApp.getTitle())
        Text(text = "After activation, enquiry the application state")
        BrandedButton(
            onClick = {
                viewModel.writeMessage("Launch - Enquiry App Status")
                enquiryAppLauncher.launch()
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Enquiry App Status")
        }

        Divider()
        Text(text = UiSection.SaleTran.getTitle())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                label = { Text(text = "Currency") },
                value = viewModel.currency,
                onValueChange = {},
                enabled = false
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                label = { Text(text = "Amount") },
                value = viewModel.amountStr,
                onValueChange = viewModel::handleInputAmt,
                isError = viewModel.amountStr.isEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        TextButton(
            onClick = { viewModel.resetRandomPosMessageId() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Set random POS message ID")
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "POS message ID") },
            value = viewModel.posMessageId,
            onValueChange = { viewModel.posMessageId = it },
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                interactionSource = switchInteractSrc,
                checked = viewModel.autoDismissTransaction,
                onCheckedChange = { viewModel.autoDismissTransaction = it },
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Auto dismiss transaction result screen -> return to the business app",
                modifier = Modifier.clickable(interactionSource = switchInteractSrc, indication = null, role = Role.Switch, onClick = {
                    viewModel.autoDismissTransaction = !viewModel.autoDismissTransaction
                })
            )
        }
        BrandedButton(
            onClick = {
                val req = PosRequest.Transaction.Sale(
                    amount = viewModel.getAmountForSale(),
                    posMessageId = viewModel.posMessageId,
                    autoDismissResult = viewModel.autoDismissTransaction
                )
                viewModel.writeMessage(req.toString())
                transactionLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Sale Request")
        }
        BrandedButton(
            onClick = {
                val req = PosRequest.Transaction.Auth(
                    amount = viewModel.getAmountForSale(),
                    posMessageId = viewModel.posMessageId,
                    autoDismissResult = viewModel.autoDismissTransaction
                )
                viewModel.writeMessage(req.toString())
                transactionLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Auth Request")
        }

        Divider()
        Text(text = UiSection.EnquiryTran.getTitle())
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Transaction ID") },
            value = viewModel.cachedTransactionId,
            onValueChange = { viewModel.cachedTransactionId = it },
        )
        BrandedButton(
            onClick = {
                val req = PosRequest.EnquiryTranStatus(viewModel.cachedTransactionId)
                viewModel.writeMessage(req.toString())
                enquiryTranLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Enquiry Transaction")
        }

        Divider()
        Text(text = UiSection.EnquiryTranWithMessageId.getTitle())
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "POS Message ID") },
            value = viewModel.cachedPosMessageId,
            onValueChange = { viewModel.cachedPosMessageId = it },
        )
        BrandedButton(
            onClick = {
                val req = PosRequest.EnquiryTranStatusWithMessageId(viewModel.cachedPosMessageId)
                viewModel.writeMessage(req.toString())
                enquiryTranWithMessageIdLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Enquiry Transaction With MessageId")
        }

        Divider()
        Text(text = UiSection.VoidTran.getTitle())
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Admin Passcode") },
            value = viewModel.merchantAdminPasscode,
            onValueChange = { viewModel.merchantAdminPasscode = it },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Transaction ID") },
            value = viewModel.cachedTransactionId,
            onValueChange = { viewModel.cachedTransactionId = it },
        )
        BrandedButton(
            onClick = {
                val req = PosRequest.Transaction.Void(
                    orgTranId = viewModel.cachedTransactionId,
                    posMessageId = viewModel.posMessageId,
                    adminPwd = viewModel.merchantAdminPasscode
                )
                viewModel.writeMessage(req.toString())
                transactionLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Void")
        }

        Divider()
        Text(text = UiSection.Settlement.getTitle())
        BrandedButton(
            onClick = {
                val req = PosRequest.Settlement(posMessageId = viewModel.posMessageId)
                viewModel.writeMessage(req.toString())
                settlementLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Settlement")
        }

        Divider()
        Text(text = UiSection.RefundTran.getTitle())
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Transaction ID") },
            value = viewModel.cachedTransactionId,
            onValueChange = { viewModel.cachedTransactionId = it },
        )
        BrandedButton(
            onClick = {
                val req = PosRequest.Transaction.Refund(
                    viewModel.cachedTransactionId,
                    viewModel.posMessageId,
                    viewModel.merchantAdminPasscode
                )
                viewModel.writeMessage(req.toString())
                transactionLauncher.launch(req)
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Refund")
        }
        Divider()
        Text(text = UiSection.ReloadConfiguration.getTitle())
        Text(text = "Reload Configuration")
        BrandedButton(
            onClick = {
                viewModel.writeMessage("Launch - Reload Configuration")
                reloadConfigurationLauncher.launch()
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Reload Configuration")
        }
        Divider()
        Text(text = UiSection.EnquiryBluetoothConnectStatus.getTitle())
        Text(text = "Enquiry Bluetooth Connection Status")
        BrandedButton(
            onClick = {
                viewModel.writeMessage("Launch - Enquiry Bluetooth Connection Status")
                enquiryBTConnectStatusLauncher.launch()
            },
            enabled = isSoftPosInstalled
        ) {
            Text(text = "Enquiry Bluetooth Connection Status")
        }
    }
}

