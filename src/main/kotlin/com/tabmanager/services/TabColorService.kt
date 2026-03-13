package com.tabmanager.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.tabmanager.model.TabColor

@State(
    name = "TabColorService",
    storages = [Storage("tabManagerColors.xml")]
)
@Service(Service.Level.PROJECT)
class TabColorService(private val project: Project) : PersistentStateComponent<TabColorService.State> {

    data class State(
        var fileColors: MutableMap<String, String> = mutableMapOf()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    fun getColorForFile(filePath: String): TabColor =
        myState.fileColors[filePath]?.let { TabColor.fromName(it) } ?: TabColor.NONE

    fun setColorForFile(filePath: String, color: TabColor) {
        if (color == TabColor.NONE) {
            myState.fileColors.remove(filePath)
        } else {
            myState.fileColors[filePath] = color.name
        }
        notifyChanged()
    }

    fun clearColorForFile(filePath: String) {
        myState.fileColors.remove(filePath)
        notifyChanged()
    }

    fun setColorForFiles(filePaths: List<String>, color: TabColor) {
        filePaths.forEach { setColorForFile(it, color) }
    }

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
        fun getInstance(project: Project): TabColorService =
            project.getService(TabColorService::class.java)
    }
}
