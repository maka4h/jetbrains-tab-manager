package com.tabmanager.ui

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.ui.JBUI
import com.tabmanager.model.TabColor
import com.tabmanager.model.TabGroup
import com.tabmanager.services.TabColorService
import com.tabmanager.services.TabGroupService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import javax.swing.*

class GroupsPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val groupService = TabGroupService.getInstance(project)
    private val colorService = TabColorService.getInstance(project)

    private val groupListModel = DefaultListModel<TabGroup>()
    private val groupList = JBList(groupListModel)

    private val fileListModel = DefaultListModel<String>()
    private val fileList = JBList(fileListModel)

    init {
        setupUI()
        refreshGroups()

        groupService.addChangeListener { refreshGroups() }
    }

    private fun setupUI() {
        groupList.cellRenderer = object : ColoredListCellRenderer<TabGroup>() {
            override fun customizeCellRenderer(
                list: JList<out TabGroup>, value: TabGroup, index: Int,
                selected: Boolean, hasFocus: Boolean
            ) {
                val color = TabColor.fromName(value.colorName)
                if (color != TabColor.NONE) {
                    icon = createColorIcon(color.color)
                }
                append(value.name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
                append("  (${value.filePaths.size} files)", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }

        groupList.addListSelectionListener {
            val selected = groupList.selectedValue
            fileListModel.clear()
            selected?.filePaths?.forEach { fileListModel.addElement(it) }
        }

        fileList.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 2) {
                    val path = fileList.selectedValue ?: return
                    openFile(path)
                }
            }
        })

        val groupDecorator = ToolbarDecorator.createDecorator(groupList)
            .setAddAction { createGroup() }
            .setRemoveAction { deleteGroup() }
            .addExtraAction(object : com.intellij.ui.AnActionButton("Rename", com.intellij.icons.AllIcons.Actions.Edit) {
                override fun actionPerformed(e: com.intellij.openapi.actionSystem.AnActionEvent) {
                    renameGroup()
                }
            })
            .addExtraAction(object : com.intellij.ui.AnActionButton("Set Color", com.intellij.icons.AllIcons.Actions.Colors) {
                override fun actionPerformed(e: com.intellij.openapi.actionSystem.AnActionEvent) {
                    setGroupColor()
                }
            })
            .createPanel()

        val fileDecorator = ToolbarDecorator.createDecorator(fileList)
            .setRemoveAction { removeFileFromGroup() }
            .disableAddAction()
            .createPanel()

        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, groupDecorator, fileDecorator)
        splitPane.dividerLocation = 200
        splitPane.resizeWeight = 0.5

        add(splitPane, BorderLayout.CENTER)
    }

    private fun refreshGroups() {
        val selected = groupList.selectedValue?.id
        groupListModel.clear()
        groupService.getAllGroups().forEach { groupListModel.addElement(it) }

        // Restore selection
        if (selected != null) {
            for (i in 0 until groupListModel.size()) {
                if (groupListModel.getElementAt(i).id == selected) {
                    groupList.selectedIndex = i
                    break
                }
            }
        }
    }

    private fun createGroup() {
        val name = Messages.showInputDialog(
            project, "Enter group name:", "Create Group", Messages.getQuestionIcon()
        ) ?: return
        if (name.isBlank()) return
        groupService.createGroup(name)
    }

    private fun deleteGroup() {
        val selected = groupList.selectedValue ?: return
        val confirm = Messages.showYesNoDialog(
            project,
            "Delete group \"${selected.name}\"?",
            "Delete Group",
            Messages.getQuestionIcon()
        )
        if (confirm == Messages.YES) {
            groupService.deleteGroup(selected.id)
        }
    }

    private fun renameGroup() {
        val selected = groupList.selectedValue ?: return
        val name = Messages.showInputDialog(
            project, "New name:", "Rename Group", Messages.getQuestionIcon(), selected.name, null
        ) ?: return
        if (name.isBlank()) return
        groupService.renameGroup(selected.id, name)
    }

    private fun setGroupColor() {
        val selected = groupList.selectedValue ?: return
        val colorOptions = TabColor.entries.map { it.displayName }.toTypedArray()
        val choice = Messages.showChooseDialog(
            project, "Select color for group \"${selected.name}\":",
            "Set Group Color", Messages.getQuestionIcon(), colorOptions, TabColor.NONE.displayName
        ) ?: return
        val color = TabColor.entries.find { it.displayName == choice } ?: return
        groupService.setGroupColor(selected.id, color)

        // Apply color to all files in this group
        colorService.setColorForFiles(selected.filePaths, color)
    }

    private fun removeFileFromGroup() {
        val group = groupList.selectedValue ?: return
        val filePath = fileList.selectedValue ?: return
        groupService.removeFileFromGroup(group.id, filePath)
        fileListModel.removeElement(filePath)
    }

    private fun openFile(path: String) {
        val vf = LocalFileSystem.getInstance().findFileByPath(path) ?: return
        FileEditorManager.getInstance(project).openFile(vf, true)
    }

    private fun createColorIcon(color: Color): Icon {
        return object : Icon {
            override fun paintIcon(c: Component?, g: java.awt.Graphics, x: Int, y: Int) {
                g.color = color
                g.fillRect(x, y, iconWidth, iconHeight)
                g.color = color.darker()
                g.drawRect(x, y, iconWidth - 1, iconHeight - 1)
            }
            override fun getIconWidth() = 12
            override fun getIconHeight() = 12
        }
    }
}
