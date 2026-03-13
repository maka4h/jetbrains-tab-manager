package com.tabmanager.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.tabmanager.model.TabColor
import com.tabmanager.services.TabColorService

class SetTabColorAction : AnAction("Set Tab Color...", "Assign a color to the current tab(s)", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)
        val colorService = TabColorService.getInstance(project)

        val selectedFiles = fileEditorManager.selectedFiles
        if (selectedFiles.isEmpty()) {
            Messages.showInfoMessage(project, "No files are currently open.", "No Open Files")
            return
        }

        val colorOptions = TabColor.entries.map { it.displayName }.toTypedArray()
        val choice = Messages.showChooseDialog(
            project,
            "Select a color for the current tab(s):",
            "Set Tab Color",
            Messages.getQuestionIcon(),
            colorOptions,
            TabColor.NONE.displayName
        ) ?: return

        val selectedColor = TabColor.entries.find { it.displayName == choice } ?: return
        selectedFiles.forEach { vf ->
            colorService.setColorForFile(vf.path, selectedColor)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
