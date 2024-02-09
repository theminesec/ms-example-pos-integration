package com.theminesec.example.pos.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.theminesec.example.pos.util.DemoApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

private const val TAG = "MsExample"

class ExampleViewModel : ViewModel() {

    // for demo setups
    private val _messages: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<String>> = _messages
    private val prettyGson = GsonBuilder().setPrettyPrinting().create()
    fun writeMessage(message: String) = viewModelScope.launch {
        val temp = _messages.value
            .toMutableList()
            .apply { add("==> $message") }

        _messages.emit(temp)
    }

    fun clearLog() = viewModelScope.launch { _messages.emit(emptyList()) }

    // demo part
    var demoApp: DemoApp by mutableStateOf(DemoApp.MSA)

    // in real app you should probably do a private setter
    // and expose set method in the viewmodel
    var activationCode by mutableStateOf("")
    var cachedTransactionId by mutableStateOf("")
    var merchantAdminPasscode by mutableStateOf("123456")
    var posMessageId by mutableStateOf("")

    val currency by mutableStateOf("USD")
    var autoDismissTransaction by mutableStateOf(true)
    var amountStr by mutableStateOf("2")
        private set

    init {
        restoreDefaultActivationCode()
        resetRandomPosMessageId()
    }

    fun handleInputAmt(incomingStr: String) {
        Log.d(TAG, "incoming: $incomingStr")
        if (incomingStr.length > 12) return
        if (incomingStr.count { c -> c == '.' } > 1) return
        incomingStr.split(".").getOrNull(1)?.let { afterDecimal ->
            if (afterDecimal.length > 2) return
        }
        amountStr = incomingStr.filter { c -> c.isDigit() || c == '.' }
    }

    fun getAmountForSale(): BigDecimal = try {
        BigDecimal(amountStr)
    } catch (e: Exception) {
        BigDecimal.ZERO
    }

    fun resetRandomPosMessageId() {
        posMessageId = UUID.randomUUID().toString()
    }

    fun changeDemoApp(demoAppName: String) {
        demoApp = DemoApp.valueOf(demoAppName)
        restoreDefaultActivationCode()
    }

    fun restoreDefaultActivationCode() {
        activationCode = demoApp.param.defaultActivationCode
    }
}
