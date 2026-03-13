package com.tabmanager.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JTabbedPane

class TabManagerToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val tabbedPane = JTabbedPane()
        tabbedPane.addTab("Groups", GroupsPanel(project))
        tabbedPane.addTab("Workspaces", WorkspacesPanel(project))

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(tabbedPane, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean = true
}
