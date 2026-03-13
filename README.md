# JetBrains Tab Manager

A powerful tab management plugin for all JetBrains IDEs — IntelliJ IDEA, GoLand, PyCharm, RustRover, WebStorm, and more.

---

## Features

### 🗂️ Tab Grouping
- Select multiple open editor tabs and group them under a named label
- View groups in the dedicated **Tab Manager** tool window (right-side panel)
- Add or remove tabs from existing groups via the tool window or right-click context menu
- Rename or delete groups at any time
- Right-click context menu on editor tabs: **"Add to Group…"** / **"Create New Group from Selected Tabs"**

### 🎨 Tab Coloring
- Assign a color to individual tabs or to all tabs in a group
- Preset palette: **Red, Blue, Green, Yellow, Orange, Purple, Cyan, Pink**
- Tab colors are reflected in the editor tab bar via IntelliJ's `EditorTabColorProvider` API
- Colors persist across IDE restarts (stored per project)
- Supports both light and dark IDE themes via `JBColor`

### 💼 Workspaces
- A **Workspace** is a named snapshot of:
  - All currently open editor tabs (file paths)
  - All visible tool windows (e.g. Terminal, Project, Git)
  - The currently active/focused editor tab
- **Save** the current state as a named workspace
- **Switch** to any saved workspace — closes all current tabs, re-opens the saved ones, restores tool window visibility, and focuses the previously active tab
- **Update** a workspace to overwrite it with the current state
- **Rename** or **Delete** workspaces at any time
- Keyboard shortcuts for quick workspace switching (see below)

### 🖥️ Tab Manager Tool Window
- Dedicated panel titled **"Tab Manager"** (docked to the right side)
- Two tabs:
  - **Groups** — lists all tab groups, their colors, and member files
  - **Workspaces** — lists all saved workspaces with Switch / Update / Rename / Delete actions
- Toolbar actions for creating new groups and saving workspaces
- Double-click a file in a group → navigates to that editor tab
- Double-click a workspace → switches to it (with confirmation)

---

## Keyboard Shortcuts

| Action | Default Shortcut |
|---|---|
| Create New Group | `Ctrl+Shift+G` |
| Set Tab Color | `Ctrl+Shift+K` |
| Save Workspace | `Ctrl+Shift+W` |
| Switch Workspace | `Ctrl+Shift+Alt+W` |

> Shortcuts can be customized in **Settings → Keymap → Tab Manager**.

---

## Installation

### From JetBrains Marketplace
1. Open your JetBrains IDE
2. Go to **Settings / Preferences → Plugins → Marketplace**
3. Search for **"Tab Manager"**
4. Click **Install** and restart the IDE

### From Source
See [Build Instructions](#build-instructions) below.

---

## Usage

### Tab Grouping
1. Open files in the editor
2. Go to **Tools → Tab Manager → Create New Group** (or `Ctrl+Shift+G`)
3. Enter a group name — all currently selected/open files are added to the group
4. In the **Tab Manager** tool window → **Groups** tab, expand the group to see its files
5. Right-click any editor tab → **Tab Manager → Add to Group…** to add more files

### Tab Coloring
1. Open a file in the editor
2. Go to **Tools → Tab Manager → Set Tab Color…** (or `Ctrl+Shift+K`)
3. Choose a color from the palette — the tab is immediately colored
4. To color all files in a group: open the **Groups** panel, select the group, click the **Set Color** toolbar button

### Workspaces
1. Set up your editor with the files and tool windows you want
2. Go to **Tools → Tab Manager → Save Workspace…** (or `Ctrl+Shift+W`)
3. Enter a workspace name and click OK
4. To switch: go to **Tools → Tab Manager → Switch Workspace…** (or `Ctrl+Shift+Alt+W`)
5. Or double-click any workspace in the **Tab Manager → Workspaces** panel

---

## Build Instructions

### Prerequisites
- JDK 17+
- Gradle 8.5+ (or use the included Gradle Wrapper)

### Steps

```bash
# Clone the repository
git clone https://github.com/maka4h/jetbrains-tab-manager.git
cd jetbrains-tab-manager

# Build the plugin
./gradlew buildPlugin

# Run in a sandboxed IDE instance (for development/testing)
./gradlew runIde
```

The built plugin ZIP will be in `build/distributions/`.

### Install from Local Build
1. Run `./gradlew buildPlugin`
2. In your IDE: **Settings → Plugins → ⚙ → Install Plugin from Disk…**
3. Select the ZIP file from `build/distributions/`
4. Restart the IDE

---

## Project Structure

```
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew / gradlew.bat
├── gradle/wrapper/
└── src/main/
    ├── kotlin/com/tabmanager/
    │   ├── model/
    │   │   ├── TabGroup.kt        — Data model for a tab group
    │   │   ├── Workspace.kt       — Data model for a workspace
    │   │   └── TabColor.kt        — Color enum with JBColor definitions
    │   ├── services/
    │   │   ├── TabGroupService.kt — Persistent service for tab groups
    │   │   ├── WorkspaceService.kt— Persistent service for workspaces
    │   │   └── TabColorService.kt — Persistent service for file colors
    │   ├── actions/
    │   │   ├── CreateGroupAction.kt
    │   │   ├── AddToGroupAction.kt
    │   │   ├── SetTabColorAction.kt
    │   │   ├── SaveWorkspaceAction.kt
    │   │   └── SwitchWorkspaceAction.kt
    │   ├── ui/
    │   │   ├── TabManagerToolWindowFactory.kt
    │   │   ├── GroupsPanel.kt
    │   │   └── WorkspacesPanel.kt
    │   └── extensions/
    │       └── TabColorProvider.kt — EditorTabColorProvider implementation
    └── resources/META-INF/
        └── plugin.xml
```

---

## Compatibility

| IDE | Minimum Version |
|---|---|
| IntelliJ IDEA | 2024.1 |
| GoLand | 2024.1 |
| PyCharm | 2024.1 |
| RustRover | 2024.1 |
| WebStorm | 2024.1 |
| All other JetBrains IDEs | 2024.1 |

---

## License

This project is licensed under the **Apache License 2.0**.  
See [LICENSE](LICENSE) for details.