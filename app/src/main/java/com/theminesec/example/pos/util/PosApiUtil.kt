package com.theminesec.example.pos.util

enum class DemoApp(val param: PosApiInitParam) {
    MSA(
        PosApiInitParam(
            packageName = "com.minesec.msa.stage",
            defaultActivationCode = "767747582904"
        )
    ),
}

data class PosApiInitParam(
    val packageName: String,
    val defaultActivationCode: String,
)
