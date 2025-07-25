# iiKits 🧰

**iiKits** is a modern, lightweight Minecraft plugin that lets you create, customize, and manage kits for your server with ease. Built for flexibility and performance, iiKits is perfect for PvP, Factions, Survival, or any server that needs reliable kit functionality.

![GitHub release](https://img.shields.io/github/v/release/iiDanto/iiKits)
![Bukkit](https://img.shields.io/badge/Bukkit-1.8--1.20-yellow)
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)

---

## ✨ Features

- 🎒 Easily create custom kits via in-game GUI
- ⏳ Async Kit Saving & Loading
- 🔒 Permission based administration panel
- 🔄 Kit previews and item replacements  
- 💾 Configurable Storage (SQLite / MySQL)

---

## 📦 Installation

### Requirements

- Minecraft Server1.21 (Bukkit, Paper, or compatible fork)
- Java 17 or higher

### Steps

1. Download the latest release from the [Releases page](https://github.com/iiDanto/iiKits/releases).
2. Place `iiKits.jar` in your server’s `plugins` folder.
3. Restart or reload your server.
4. Configure the plugin in `/plugins/iiKits/config.yml` or in-game.

---

## 🕹️ Usage

### Basic Commands

| Command               | Description                   |
|-----------------------|-------------------------------|
| `/kit`                | Opens the main kit menu       |
| `/kit view <name>`    | View a kit's contents         |
| `/kit save <name>`    | Save a new kit from inventory |
| `/kit load <name>`    | Loads a kit                   |
| `/k<int>`             | Quick Loads a Kit             |

### Permissions

### Permissions

| Permission                | Description                                     |
|---------------------------|-------------------------------------------------|
| `iikits.kits.view`        | Allows players to view other players' kits     |
| `iikits.kits.preview`     | Allows players to preview their own kits       |
| `iikits.kits.save`        | Allows players to save their current inventory as a kit |
| `iikits.kits.load`        | Allows players to load a kit into their inventory |
| `iikits.kitroom`          | Allows players to open the kit room GUI        |
| `iikits.admin`            | (Optional) Full access to all kit commands     |

---

## 📂 Configuration

iiKits is fully configurable via the `config.yml` file. You can define:

- Rekit Announcements
- Rekit & Regear Announcement Radius
- Rekit Content Whitelist
- Plugin Prefix & Colour Mappings

**Example `config.yml`:**

```yaml
# iiKits General Configuration
# Credits:
# Github: iiDanto
# Discord: iiDanto - iidanto
# Minecraft: iiDanto

storage:
  type: "SQLite" # Options: SQLite, MySQL
  credentials:
    url: ""
    username: ""
    password: ""
general:
  rekits:
    announce-rekits: true
    distance: 30
messages:
  prefix: "<gray>[<#28a8ff><bold>V<white><bold>C<gray>] <#28a8ff>» "
  colour: "<#28a8ff>" # Ensure you put the HEX Colour Code inside brackets (<>)!
whitelist: # ALl items that will be given to a player upon regear.
  - ENDER_PEARL
  - END_CRYSTAL
  - OBSIDIAN
  - GLOWSTONE
  - SPLASH_POTION
  - NETHERITE_PICKAXE
  - NETHERITE_SWORD
  - SHIELD
  - NETHERITE_AXE
  - NETHERITE_PICKAXE
  - GOLDEN_APPLE
  - EXPERIENCE_BOTTLE
  - TIPPED_ARROW
  - CROSSBOW
  - BOW
```
