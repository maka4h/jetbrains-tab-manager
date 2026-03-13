package com.tabmanager.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.tabmanager.services.TabGroupService

class AddToGroupAction : AnAction("Add to Group...", "Add current tab(s) to an existing group", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)
        val groupService = TabGroupService.getInstance(project)

        val groups = groupService.getAllGroups()
        if (groups.isEmpty()) {
            Messages.showInfoMessage(project, "No groups exist yet. Create a group first.", "No Groups")
            return
        }

        val selectedFiles = fileEditorManager.selectedFiles
        if (selectedFiles.isEmpty()) {
            Messages.showInfoMessage(project, "No files are currently open.", "No Open Files")
            return
        }

        val groupNames = groups.map { it.name }.toTypedArray()
        val choice = Messages.showChooseDialog(
            project,
            "Select a group to add the current file(s) to:",
            "Add to Group",
            Messages.getQuestionIcon(),
            groupNames,
            groupNames.firstOrNull()
        )

        if (choice == null) return

        val selectedGroup = groups.find { it.name == choice } ?: return
        selectedFiles.forEach { vf ->
            groupService.addFileToGroup(selectedGroup.id, vf.path)
        }

        Messages.showInfoMessage(
            project,
            "Added ${selectedFiles.size} file(s) to group \"${selectedGroup.name}\".",
            "Files Added"
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
