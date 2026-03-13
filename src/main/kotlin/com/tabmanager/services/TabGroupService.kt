package com.tabmanager.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.tabmanager.model.TabColor
import com.tabmanager.model.TabGroup
import java.util.UUID

@State(
    name = "TabGroupService",
    storages = [Storage("tabManagerGroups.xml")]
)
@Service(Service.Level.PROJECT)
class TabGroupService(private val project: Project) : PersistentStateComponent<TabGroupService.State> {

    data class State(
        var groups: MutableList<TabGroup> = mutableListOf()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    fun getAllGroups(): List<TabGroup> = myState.groups.toList()

    fun createGroup(name: String, filePaths: List<String> = emptyList()): TabGroup {
        val group = TabGroup(
            id = UUID.randomUUID().toString(),
            name = name,
            filePaths = filePaths.toMutableList()
        )
        myState.groups.add(group)
        notifyChanged()
        return group
    }

    fun renameGroup(groupId: String, newName: String) {
        findGroupById(groupId)?.let {
            it.name = newName
            notifyChanged()
        }
    }

    fun deleteGroup(groupId: String) {
        myState.groups.removeIf { it.id == groupId }
        notifyChanged()
    }

    fun addFileToGroup(groupId: String, filePath: String) {
        findGroupById(groupId)?.let {
            if (!it.filePaths.contains(filePath)) {
                it.filePaths.add(filePath)
                notifyChanged()
            }
        }
    }

    fun removeFileFromGroup(groupId: String, filePath: String) {
        findGroupById(groupId)?.let {
            it.filePaths.remove(filePath)
            notifyChanged()
        }
    }

    fun setGroupColor(groupId: String, color: TabColor) {
        findGroupById(groupId)?.let {
            it.colorName = color.name
            notifyChanged()
        }
    }

    fun findGroupById(groupId: String): TabGroup? =
        myState.groups.find { it.id == groupId }

    fun findGroupsForFile(filePath: String): List<TabGroup> =
        myState.groups.filter { it.filePaths.contains(filePath) }

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
        fun getInstance(project: Project): TabGroupService =
            project.getService(TabGroupService::class.java)
    }
}
