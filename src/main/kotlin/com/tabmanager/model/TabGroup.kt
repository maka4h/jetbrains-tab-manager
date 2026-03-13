package com.tabmanager.model

import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection

@Tag("tabGroup")
data class TabGroup(
    var id: String = "",
    var name: String = "",
    var colorName: String = "NONE",
    @XCollection(style = XCollection.Style.v2)
    var filePaths: MutableList<String> = mutableListOf()
)
