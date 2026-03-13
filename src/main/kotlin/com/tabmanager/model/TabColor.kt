package com.tabmanager.model

import com.intellij.ui.JBColor
import java.awt.Color

enum class TabColor(val displayName: String, val color: JBColor) {
    NONE("None", JBColor(Color(0, 0, 0, 0), Color(0, 0, 0, 0))),
    RED("Red", JBColor(Color(255, 100, 100), Color(200, 70, 70))),
    BLUE("Blue", JBColor(Color(100, 149, 237), Color(70, 119, 207))),
    GREEN("Green", JBColor(Color(100, 200, 100), Color(70, 160, 70))),
    YELLOW("Yellow", JBColor(Color(255, 230, 100), Color(200, 180, 70))),
    ORANGE("Orange", JBColor(Color(255, 165, 80), Color(200, 130, 50))),
    PURPLE("Purple", JBColor(Color(180, 100, 220), Color(140, 70, 180))),
    CYAN("Cyan", JBColor(Color(80, 200, 220), Color(50, 160, 180))),
    PINK("Pink", JBColor(Color(255, 150, 190), Color(200, 110, 150)));

    companion object {
        fun fromName(name: String): TabColor = entries.find { it.name == name } ?: NONE
    }
}
