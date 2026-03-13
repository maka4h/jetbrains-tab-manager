package com.tabmanager.model

import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection

@Tag("workspace")
data class Workspace(
    var id: String = "",
    var name: String = "",
    @XCollection(style = XCollection.Style.v2)
    var openFilePaths: MutableList<String> = mutableListOf(),
    var activeFilePath: String = "",
    @XCollection(style = XCollection.Style.v2)
    var visibleToolWindows: MutableList<String> = mutableListOf()
)
