package com.tabmanager.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.tabmanager.services.WorkspaceService

class SwitchWorkspaceAction : AnAction("Switch Workspace...", "Switch to a saved workspace", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val workspaceService = WorkspaceService.getInstance(project)

        val workspaces = workspaceService.getAllWorkspaces()
        if (workspaces.isEmpty()) {
            Messages.showInfoMessage(project, "No workspaces saved yet. Save a workspace first.", "No Workspaces")
            return
        }

        val workspaceNames = workspaces.map { it.name }.toTypedArray()
        val choice = Messages.showChooseDialog(
            project,
            "Select a workspace to switch to:",
            "Switch Workspace",
            Messages.getQuestionIcon(),
            workspaceNames,
            workspaceNames.firstOrNull()
        ) ?: return

        val selected = workspaces.find { it.name == choice } ?: return
        workspaceService.switchToWorkspace(selected.id)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
