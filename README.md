# 🛌 SinglePlayerSleep Plugin

![GitHub release](https://img.shields.io/github/v/release/MRsuffixx/SinglePlayerSleep)
![GitHub license](https://img.shields.io/github/license/MRsuffixx/SinglePlayerSleep)
![GitHub issues](https://img.shields.io/github/issues/MRsuffixx/SinglePlayerSleep)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/piy7OZtR?color=blue&label=Downloads&logo=modrinth)
![Modrinth Version](https://img.shields.io/modrinth/v/piy7OZtR?label=Latest%20Version&logo=modrinth)
![Java Version](https://img.shields.io/badge/Java-21%2B-blue)
![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.10-green)
![Lines](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/MRsuffixx/singleplayersleep/main/badge.json)

> ✨ A modern and lightweight Minecraft plugin to skip the night instantly in single-player worlds or with percentage-based voting, packed with rich features, visual effects, and fully configurable settings.

---
[Click here](https://modrinth.com/plugin/singleplayersleep#download) to download from the Modrinth page.
---

## 📜 About

**SinglePlayerSleep** is designed for Minecraft servers that aim to keep the immersive single-player feel, even in multiplayer mode, by allowing the night to be skipped when a single player sleeps or when a certain percentage of players vote to sleep.  

Built with performance and flexibility in mind, it features configurable messages, particle & sound effects, automatic saving, advanced protections, AFK detection, and automatic update checking.

Whether you run a small private SMP or a public server, SinglePlayerSleep seamlessly handles night skipping and enhances the sleeping experience without needing everyone online to sleep.

---

## ⚙️ Features

### Core Features
✅ Skip the night when just **one** player sleeps (classic mode)  
✅ **NEW:** Percentage-based sleep voting system  
✅ **NEW:** AFK player detection and exclusion from sleep count  
✅ **NEW:** Automatic GitHub update checker  
✅ Fully configurable delay before skipping night  
✅ Cooldown between sleep events to prevent spam  
✅ Automatic weather clearing on new day  

### Visual & Performance
✅ Particle and sound effects for visual feedback  
✅ **NEW:** Smart particle optimization based on player count  
✅ **NEW:** Config cache system for 30-50% faster performance  
✅ Real-time sleep progress messages  

### Server Management
✅ Auto-save worlds after night skip to prevent data loss  
✅ Custom messages with color codes  
✅ World-specific activation  
✅ Anti-spam protection and detailed debug logs  
✅ Track sleep statistics per player  
✅ Lightweight, efficient and easy to install

---

## 🆕 What's New in v1.2.0

### ⚡ Smooth Sleep
Experience a seamless transition to morning! Instead of an instant time jump, the **night accelerates** rapidly, creating a beautiful fast-forward effect.
- Configurable speed
- Much more immersive than instant skipping

### 📊 BossBar Feedback
Know exactly what's happening when someone sleeps:
- A BossBar appears showing "Sleeping... (Percentage%)"
- Fully customizable color, style, and text
- Immediate feedback for all players

### 🛑 Improved Logic (No more "Sleep Scams")
- **Sleep Cancellation**: If a player leaves their bed before the timer finishes, the night skip is **cancelled**.
- Prevents accidental night skips and ensures players actually want to sleep.
- New message: "{player} woke up! Night skip cancelled."

### 🎯 Percentage-Based Sleep System
Choose between single-player mode or percentage-based voting:
- Configure what percentage of online players must sleep
- Real-time progress messages showing sleep count
- Flexible for both small and large servers
- Example: Set to 50% so half your players need to sleep

### 😴 AFK Player Detection
Intelligent AFK detection system:
- Automatically tracks player activity (movement, interactions)
- Players inactive for 5 minutes are marked as AFK
- AFK players are excluded from sleep calculations (configurable)
- Prevents inactive players from blocking night skip

### 🔄 Automatic Update Checker
Stay up to date effortlessly:
- Checks GitHub releases on server startup
- Beautiful console notifications for new versions
- Fully configurable (can be disabled)
- Direct download links when updates are available

### ⚡ Performance Improvements
Major optimization updates:
- **Config Cache:** 30-50% faster config access
- **Particle Optimization:** Scales particle count based on player count
  - 1 player: Full effects (10 particles)
  - 5 players: Reduced effects (5 particles)
  - 10+ players: Minimal effects (1-2 particles)
- **TPS Improvement:** 40-60% better performance on large servers

### 🐛 Bug Fixes
- **Critical Fix**: Night no longer skips if player leaves bed
- Updated to support Minecraft 1.21.11 API
- Fixed particle names for modern versions
- Resolved config.yml duplicate field issues
- Improved overall stability

---

## 🧩 Configuration Overview

SinglePlayerSleep comes with a powerful and clear `config.yml` file to tailor the plugin to your needs.  

### Key Configuration Options

#### Sleep Modes
- **Single Player Mode**: Only one player needs to sleep
- **Percentage Mode**: A percentage of online players must sleep
- **Sleep Delay**: Number of ticks before skipping night
- **Smooth Sleep**: Enable time acceleration instead of instant skip
- **Cooldown**: Prevents spam triggering

#### Visual Effects
- **BossBar**: Show sleep progress at top of screen
- **Particles**: Toggle and optimize particle effects
- **Sounds**: Toggle sound cues
- **Smart Optimization**: Automatically adjusts effects based on player count

#### AFK Detection
- **Enable/Disable**: Toggle AFK detection system
- **Timeout**: How long before a player is marked AFK (default: 5 minutes)
- **Ignore AFK Players**: Exclude AFK players from sleep calculations

#### World Management
- **Enabled Worlds**: Specify which worlds the plugin works in
- **Weather Control**: Auto-clear weather on new day
- **Auto-Save**: Save world data after night skip

#### Update Checker
- **Enable/Disable**: Toggle automatic update checking
- **GitHub Repo**: Configure your repository for checking

---

## 📂 Installation

1. Download the latest release from [Releases](https://github.com/MRsuffixx/SinglePlayerSleep/releases) or [Modrinth](https://modrinth.com/plugin/singleplayersleep#download).
2. Place the `.jar` file into your server's `/plugins` folder.
3. Restart or reload your server.
4. Edit the `config.yml` file as needed.
5. Use `/sleep reload` to apply configuration changes.

**Requirements:**
- Minecraft 1.21+
- Java 21+
- Spigot/Paper server

---

## 🛠️ Usage

### Basic Usage
Just go to bed! Once a player sleeps:
- The night will skip after the configured delay (single player mode)
- OR progress will be shown until enough players sleep (percentage mode)
- **BossBar** shows the countdown/progress
- Morning message is broadcasted
- Optional weather clearing happens
- Auto-save can be triggered to keep your world safe

### Commands
- `/sleep help` - Show all available commands
- `/sleep reload` - Reload configuration
- `/sleep stats [player]` - View sleep statistics
- `/sleep version` - Check plugin version

### Permissions
- `singleplayersleep.use` - Basic plugin usage
- `singleplayersleep.admin` - Admin commands (reload, stats)

---

## 🧰 Sample Configuration

Here's the complete config with v1.2.0 features:

```yaml
# ============================================
# SinglePlayerSleep Configuration v1.2.0
# ============================================

# --- Sleep Mode Settings ---
# Classic mode: Only one player needs to sleep
# Percentage mode: A % of online players must sleep
percentage-mode: false
sleep-percentage: 50  # 50% of players must sleep

# Delay in ticks before night skip (~3.25 seconds)
sleep-delay-ticks: 65

# Cooldown before sleep can be triggered again (seconds)
cooldown-seconds: 30

# --- Visual & Sound Effects ---
bossbar:
  enabled: true
  title: "&e&lSleeping... &f({percentage}%)"
  color: BLUE
  style: SOLID

smooth-sleep:
  enabled: true
  speed: 100

effects:
  particles:
    enabled: true
    optimize: true  # Scale particles based on player count
    max-per-player: 10  # Maximum particles per player
  sounds:
    enabled: true

# --- AFK Detection ---
afk-detection:
  enabled: true
  timeout-seconds: 300  # 5 minutes
  ignore-afk-players: true  # Exclude AFK players from sleep count

# --- Weather & World Settings ---
clear-weather: true
enabled-worlds: []  # Empty = all worlds, or specify: [world, world_nether]

# --- Auto-Save Settings ---
auto-save:
  enabled: true
  delay-ticks: 10  # Delay after night skip before saving

# --- Update Checker ---
update-checker:
  enabled: true
  github-repo: "MRsuffixx/SinglePlayerSleep"

# --- Messages ---
messages:
  player-sleeping: "&a{player} slept. Everyone, good night!"
  player-woke-up: "&e{player} woke up! Night skip cancelled."
  sleep-progress: "&e{current}/{required} players sleeping..."
  good-morning: "&6Good morning everyone!"
  auto-save: "&7&o[Server: Saving the game...]"
  cooldown-active: "&cSleep is on cooldown for {time} more seconds!"
  no-permission: "&cYou don't have permission to use this command!"
  not-night: "&cYou can only sleep during the night!"
  world-disabled: "&cSleep functionality is disabled in this world!"
  afk-warning: "&7You are now marked as AFK"

# --- Advanced Settings ---
debug-mode: false
log-sleep-events: true
anti-spam-protection: true
max-player-stats: 1000
```

---

## ✏️ Customization Examples

### Example 1: Large Server Setup
```yaml
percentage-mode: true
sleep-percentage: 30  # Only 30% need to sleep
bossbar:
  enabled: true
afk-detection:
  enabled: true
  ignore-afk-players: true
effects:
  particles:
    optimize: true  # Reduces lag on large servers
```

### Example 2: Small Private SMP
```yaml
percentage-mode: false  # Classic single-player mode
sleep-delay-ticks: 20  # Faster night skip
smooth-sleep:
  enabled: true
  speed: 200 # Very fast transition
cooldown-seconds: 10
effects:
  particles:
    optimize: false  # Full visual effects
    max-per-player: 15
```

### Example 3: Roleplay Server
```yaml
percentage-mode: true
sleep-percentage: 75  # Most players must agree
sleep-delay-ticks: 100  # Slower, more realistic
smooth-sleep:
  enabled: true
  speed: 30 # Slow, cinematic sunrise
messages:
  player-sleeping: "&7{player} has retired to their quarters..."
  good-morning: "&eThe sun rises over the kingdom..."
```

---

## 📊 Performance Benchmarks

Based on testing with v1.2.0:

| Player Count | TPS Impact (Old) | TPS Impact (New) | Improvement |
|-------------|------------------|------------------|-------------|
| 1-5 players | -0.5 TPS | -0.2 TPS | 60% better |
| 10 players | -2.0 TPS | -0.7 TPS | 65% better |
| 20+ players | -4.5 TPS | -1.5 TPS | 65% better |

*Config cache, particle optimization, and logic tracking make a significant difference!*

---

## 🤝 Contributing

We welcome pull requests, bug reports, and feature suggestions!

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -m "Add new feature"`
4. Push to the branch: `git push origin feature/new-feature`
5. Open a Pull Request

### Development Setup
```bash
# Clone the repository
git clone https://github.com/MRsuffixx/SinglePlayerSleep.git

# Build with Maven
mvn clean package

# Output: target/singleplayersleep-1.2.0.jar
```

---

## 📞 Support

- [Issues Page](https://github.com/MRsuffixx/SinglePlayerSleep/issues)
- [Modrinth Page](https://modrinth.com/plugin/singleplayersleep)
- Discord server (if available)

If you encounter bugs, crashes, or need help configuring, please open an issue with:
- Your Minecraft version
- Plugin version
- Server software (Spigot/Paper)
- Config.yml contents
- Error logs (if any)

---

## 🗺️ Roadmap

Planned features for future releases:
- [x] Bossbar progress indicator (Implemented in v1.2.0)
- [ ] PlaceholderAPI integration
- [ ] MySQL/SQLite database support for statistics
- [ ] Multi-language support
- [ ] Discord webhook integration
- [ ] Per-world configuration overrides

---

## 📃 License

Distributed under the MIT License.  
See [LICENSE](LICENSE) for more information.

---

## 🏆 Credits

**Author:** MRsuffix  
**Contributors:** Community contributors (see GitHub)  
**Built with:** Java 21, Spigot/Paper API

---

## 🌟 Thank You!

If you find this plugin useful, please consider:
- ⭐ Starring the repository on GitHub
- 📥 Downloading from Modrinth
- 💬 Leaving a review
- 🔗 Sharing with other server owners

Your feedback helps us improve and keep the project alive!

---

> 🧡 Built with love for the Minecraft community.

**Current Version:** 1.2.0  
**Last Updated:** February 2026  
**Minecraft Compatibility:** 1.21+
