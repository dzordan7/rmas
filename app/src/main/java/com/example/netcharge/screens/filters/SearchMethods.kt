package com.example.netcharge.screens.filters

import com.example.netcharge.models.NetCharge

fun searchNetChargeByDescription(
    netcharge: MutableList<NetCharge>,
    query: String
):List<NetCharge>{
    val regex = query.split(" ").joinToString(".*"){
        Regex.escape(it)
    }.toRegex(RegexOption.IGNORE_CASE)
    return netcharge.filter { netcharge ->
        regex.containsMatchIn(netcharge.description)
    }
}
