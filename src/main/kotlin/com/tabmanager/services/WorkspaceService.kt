package com.tabmanager.services

import com.intellij.openapi.components.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.ToolWindowManager
import com.tabmanager.model.Workspace
import java.util.UUID

@State(
    name = "WorkspaceService",
    storages = [Storage("tabManagerWorkspaces.xml")]
)
@Service(Service.Level.PROJECT)
class WorkspaceService(private val project: Project) : PersistentStateComponent<WorkspaceService.State> {

    data class State(
        var workspaces: MutableList<Workspace> = mutableListOf()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    fun getAllWorkspaces(): List<Workspace> = myState.workspaces.toList()

    fun saveCurrentAsWorkspace(name: String): Workspace {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val toolWindowManager = ToolWindowManager.getInstance(project)

        val openFiles = fileEditorManager.openFiles.map { it.path }
        val activeFile = fileEditorManager.selectedFiles.firstOrNull()?.path ?: ""

        val visibleToolWindows = toolWindowManager.toolWindowIds
            .filter { id ->
                runCatching { toolWindowManager.getToolWindow(id)?.isVisible == true }.getOrDefault(false)
            }

        val workspace = Workspace(
            id = UUID.randomUUID().toString(),
            name = name,
            openFilePaths = openFiles.toMutableList(),
            activeFilePath = activeFile,
            visibleToolWindows = visibleToolWindows.toMutableList()
        )
        myState.workspaces.add(workspace)
        notifyChanged()
        return workspace
    }

    fun switchToWorkspace(workspaceId: String) {
        val workspace = findWorkspaceById(workspaceId) ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val localFs = LocalFileSystem.getInstance()

        // Close all currently open files
        fileEditorManager.openFiles.forEach { fileEditorManager.closeFile(it) }

        // Reopen saved files; track the active VF
        var activeVf = workspace.openFilePaths
            .mapNotNull { localFs.findFileByPath(it) }
            .onEach { vf -> runCatching { fileEditorManager.openFile(vf, false) } }
            .firstOrNull { it.path == workspace.activeFilePath }

        // Focus the active file
        activeVf?.let { fileEditorManager.openFile(it, true) }

        // Restore tool window visibility
        toolWindowManager.toolWindowIds.forEach { id ->
            runCatching {
                val tw = toolWindowManager.getToolWindow(id) ?: return@forEach
                if (workspace.visibleToolWindows.contains(id)) {
                    tw.show(null)
                } else {
                    tw.hide(null)
                }
            }
        }

        notifyChanged()
    }

    fun updateWorkspace(workspaceId: String) {
        val existing = findWorkspaceById(workspaceId) ?: return
        val index = myState.workspaces.indexOfFirst { it.id == workspaceId }
        if (index < 0) return

        val fileEditorManager = FileEditorManager.getInstance(project)
        val toolWindowManager = ToolWindowManager.getInstance(project)

        existing.openFilePaths = fileEditorManager.openFiles.map { it.path }.toMutableList()
        existing.activeFilePath = fileEditorManager.selectedFiles.firstOrNull()?.path ?: ""
        existing.visibleToolWindows = toolWindowManager.toolWindowIds
            .filter { id ->
                runCatching { toolWindowManager.getToolWindow(id)?.isVisible == true }.getOrDefault(false)
            }.toMutableList()

        notifyChanged()
    }

    fun renameWorkspace(workspaceId: String, newName: String) {
        findWorkspaceById(workspaceId)?.let {
            it.name = newName
            notifyChanged()
        }
    }

    fun deleteWorkspace(workspaceId: String) {
        myState.workspaces.removeIf { it.id == workspaceId }
        notifyChanged()
    }

    fun findWorkspaceById(workspaceId: String): Workspace? =
        myState.workspaces.find { it.id == workspaceId }

    private val listeners = mutableListOf<() -> Unit>()

    fun addChangeListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeChangeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyChanged() {
        listeners.forEach { it() }
    }

    companion object {
        fun getInstance(project: Project): WorkspaceService =
            project.getService(WorkspaceService::class.java)
    }
}
