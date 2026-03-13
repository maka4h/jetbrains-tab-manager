package com.tabmanager.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.tabmanager.services.TabGroupService

class CreateGroupAction : AnAction("Create New Group", "Create a new tab group from selected tabs", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)
        val groupService = TabGroupService.getInstance(project)

        val name = Messages.showInputDialog(
            project,
            "Enter group name:",
            "Create New Tab Group",
            Messages.getQuestionIcon()
        ) ?: return

        if (name.isBlank()) {
            Messages.showErrorDialog(project, "Group name cannot be empty.", "Invalid Name")
            return
        }

        val openFiles = fileEditorManager.selectedFiles.map { it.path }
        groupService.createGroup(name, openFiles)
        Messages.showInfoMessage(
            project,
            "Group \"$name\" created with ${openFiles.size} file(s).",
            "Group Created"
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
