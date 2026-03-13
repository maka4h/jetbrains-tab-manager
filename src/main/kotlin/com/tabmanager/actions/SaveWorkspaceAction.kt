package com.tabmanager.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.tabmanager.services.WorkspaceService

class SaveWorkspaceAction : AnAction("Save Workspace...", "Save the current tabs and views as a named workspace", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val workspaceService = WorkspaceService.getInstance(project)

        val name = Messages.showInputDialog(
            project,
            "Enter workspace name:",
            "Save Workspace",
            Messages.getQuestionIcon()
        ) ?: return

        if (name.isBlank()) {
            Messages.showErrorDialog(project, "Workspace name cannot be empty.", "Invalid Name")
            return
        }

        val workspace = workspaceService.saveCurrentAsWorkspace(name)
        Messages.showInfoMessage(
            project,
            "Workspace \"${workspace.name}\" saved with ${workspace.openFilePaths.size} open file(s).",
            "Workspace Saved"
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
