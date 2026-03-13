package com.tabmanager.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.tabmanager.model.Workspace
import com.tabmanager.services.WorkspaceService
import java.awt.BorderLayout
import javax.swing.*

class WorkspacesPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val workspaceService = WorkspaceService.getInstance(project)

    private val listModel = DefaultListModel<Workspace>()
    private val workspaceList = JBList(listModel)

    init {
        setupUI()
        refreshWorkspaces()
        workspaceService.addChangeListener { refreshWorkspaces() }
    }

    private fun setupUI() {
        workspaceList.cellRenderer = object : ColoredListCellRenderer<Workspace>() {
            override fun customizeCellRenderer(
                list: JList<out Workspace>, value: Workspace, index: Int,
                selected: Boolean, hasFocus: Boolean
            ) {
                append(value.name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
                append("  (${value.openFilePaths.size} files)", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }

        workspaceList.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 2) {
                    switchToSelected()
                }
            }
        })

        val decorator = ToolbarDecorator.createDecorator(workspaceList)
            .setAddAction { saveWorkspace() }
            .setRemoveAction { deleteWorkspace() }
            .addExtraAction(object : com.intellij.ui.AnActionButton("Switch", com.intellij.icons.AllIcons.Actions.Forward) {
                override fun actionPerformed(e: com.intellij.openapi.actionSystem.AnActionEvent) {
                    switchToSelected()
                }
            })
            .addExtraAction(object : com.intellij.ui.AnActionButton("Rename", com.intellij.icons.AllIcons.Actions.Edit) {
                override fun actionPerformed(e: com.intellij.openapi.actionSystem.AnActionEvent) {
                    renameWorkspace()
                }
            })
            .addExtraAction(object : com.intellij.ui.AnActionButton("Update", com.intellij.icons.AllIcons.Actions.Refresh) {
                override fun actionPerformed(e: com.intellij.openapi.actionSystem.AnActionEvent) {
                    updateWorkspace()
                }
            })
            .createPanel()

        add(decorator, BorderLayout.CENTER)
    }

    private fun refreshWorkspaces() {
        val selected = workspaceList.selectedValue?.id
        listModel.clear()
        workspaceService.getAllWorkspaces().forEach { listModel.addElement(it) }

        if (selected != null) {
            for (i in 0 until listModel.size()) {
                if (listModel.getElementAt(i).id == selected) {
                    workspaceList.selectedIndex = i
                    break
                }
            }
        }
    }

    private fun saveWorkspace() {
        val name = Messages.showInputDialog(
            project, "Enter workspace name:", "Save Workspace", Messages.getQuestionIcon()
        ) ?: return
        if (name.isBlank()) return
        workspaceService.saveCurrentAsWorkspace(name)
    }

    private fun switchToSelected() {
        val selected = workspaceList.selectedValue ?: return
        val confirm = Messages.showYesNoDialog(
            project,
            "Switch to workspace \"${selected.name}\"? This will close all current tabs.",
            "Switch Workspace",
            Messages.getQuestionIcon()
        )
        if (confirm == Messages.YES) {
            workspaceService.switchToWorkspace(selected.id)
        }
    }

    private fun renameWorkspace() {
        val selected = workspaceList.selectedValue ?: return
        val name = Messages.showInputDialog(
            project, "New name:", "Rename Workspace", Messages.getQuestionIcon(), selected.name, null
        ) ?: return
        if (name.isBlank()) return
        workspaceService.renameWorkspace(selected.id, name)
    }

    private fun updateWorkspace() {
        val selected = workspaceList.selectedValue ?: return
        val confirm = Messages.showYesNoDialog(
            project,
            "Update workspace \"${selected.name}\" with the current state?",
            "Update Workspace",
            Messages.getQuestionIcon()
        )
        if (confirm == Messages.YES) {
            workspaceService.updateWorkspace(selected.id)
        }
    }

    private fun deleteWorkspace() {
        val selected = workspaceList.selectedValue ?: return
        val confirm = Messages.showYesNoDialog(
            project,
            "Delete workspace \"${selected.name}\"?",
            "Delete Workspace",
            Messages.getQuestionIcon()
        )
        if (confirm == Messages.YES) {
            workspaceService.deleteWorkspace(selected.id)
        }
    }
}
