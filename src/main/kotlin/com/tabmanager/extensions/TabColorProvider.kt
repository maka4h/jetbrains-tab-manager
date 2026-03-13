package com.tabmanager.extensions

import com.intellij.openapi.fileEditor.impl.EditorTabColorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tabmanager.model.TabColor
import com.tabmanager.services.TabColorService
import com.tabmanager.services.TabGroupService
import java.awt.Color

class TabColorProvider : EditorTabColorProvider {

    override fun getEditorTabColor(project: Project, file: VirtualFile): Color? {
        val colorService = TabColorService.getInstance(project)
        val groupService = TabGroupService.getInstance(project)

        // Check direct file color first
        val directColor = colorService.getColorForFile(file.path)
        if (directColor != TabColor.NONE) {
            return directColor.color
        }

        // Check if file belongs to a group with a color
        val groups = groupService.findGroupsForFile(file.path)
        for (group in groups) {
            val groupColor = TabColor.fromName(group.colorName)
            if (groupColor != TabColor.NONE) {
                return groupColor.color
            }
        }

        return null
    }
}
