# 🛌 SinglePlayerSleep Plugin

![GitHub release](https://img.shields.io/github/v/release/MRsuffixx/SinglePlayerSleep)
![GitHub license](https://img.shields.io/github/license/MRsuffixx/SinglePlayerSleep)
![GitHub issues](https://img.shields.io/github/issues/MRsuffixx/SinglePlayerSleep)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/piy7OZtR?color=blue&label=Downloads&logo=modrinth)
![Modrinth Version](https://img.shields.io/modrinth/v/piy7OZtR?label=Latest%20Version&logo=modrinth)
![Java Version](https://img.shields.io/badge/Java-21%2B-blue)

> ✨ A modern and lightweight Minecraft plugin to skip the night instantly in single-player worlds, packed with rich features, visual effects, and fully configurable settings.

---
[Click here](https://modrinth.com/plugin/singleplayersleep#download) to download from the Modrinth page.
---
## 📜 About

**SinglePlayerSleep** is designed for Minecraft servers that aim to keep the immersive single-player feel, even in multiplayer mode, by allowing the night to be skipped when a single player sleeps.  
Built with performance and flexibility in mind, it features configurable messages, particle & sound effects, automatic saving, and advanced protections.

Whether you run a small private SMP or a public server, SinglePlayerSleep seamlessly handles night skipping and enhances the sleeping experience without needing everyone online to sleep.

---

## ⚙️ Features

✅ Skip the night when just **one** player sleeps  
✅ Fully configurable delay before skipping night  
✅ Cooldown between sleep events to prevent spam  
✅ Automatic weather clearing on new day  
✅ Particle and sound effects for visual feedback  
✅ Auto-save worlds after night skip to prevent data loss  
✅ Custom messages with color codes  
✅ World-specific activation  
✅ Anti-spam protection and detailed debug logs  
✅ Track sleep statistics per player  
✅ Lightweight, efficient and easy to install

---

## 🧩 Configuration Overview

SinglePlayerSleep comes with a powerful and clear `config.yml` file to tailor the plugin to your needs.  
Here’s a summary of the key configurable options:

- **Sleep Delay**: Number of ticks before skipping the night after a player sleeps.  
- **Cooldown**: Prevents repeated triggering of sleep in quick succession.  
- **Weather Control**: Option to automatically clear weather each morning.  
- **Worlds**: Enable or disable the plugin in specific worlds.  
- **Visual & Sound Effects**: Toggle particles and sound cues to enhance the experience.  
- **Messages**: Customize all server messages for a personal touch.  
- **Auto-Save**: Enable auto-saving and configure the timing to protect your data.  
- **Advanced Settings**: Logging, anti-spam, and statistics for power users.

> 📌 **Tip:** See the [`config.yml`](#-sample-config) section below for a detailed example.

---

## 📂 Installation

1. Download the latest release from [Releases](https://github.com/MRsuffixx/SinglePlayerSleep/releases).
2. Place the `.jar` file into your server’s `/plugins` folder.
3. Restart or reload your server.
4. Edit the `config.yml` file as needed and reload the plugin.

That’s it! The plugin is now ready to use.

---

## 🛠️ Usage

Just go to bed!  
Once a player sleeps:
- The night will skip after the configured delay.
- Morning message is broadcasted.
- Optional weather clearing happens.
- Auto-save can be triggered to keep your world safe.

If another player tries to trigger sleep before the cooldown ends, a friendly cooldown message will appear.

---

## 🧰 Sample Config

Here’s a simplified view of the actual config (default values):

```yaml
# Delay in ticks before night skip (~3.25 seconds)
sleep-delay-ticks: 65

# Cooldown before sleep can be triggered again
cooldown-seconds: 30

# Clear weather on new day
clear-weather: true

# Enable debug logging
debug-mode: false

# Enabled worlds (empty means all)
enabled-worlds: []

auto-save:
  enabled: true
  delay-ticks: 10

effects:
  particles:
    enabled: true
  sounds:
    enabled: true

messages:
  player-sleeping: "&a{player} slept. Everyone, good night!"
  good-morning: "&6Good morning everyone!"
  auto-save: "&7&o[Server: Saving the game...]"
  cooldown-active: "&cSleep is on cooldown for {time} more seconds!"
  no-permission: "&cYou don't have permission to use this command!"
  not-night: "&cYou can only sleep during the night!"
  world-disabled: "&cSleep functionality is disabled in this world!"

auto-save-delay-ticks: 10
max-player-stats: 1000
anti-spam-protection: true
log-sleep-events: true
````

> ⚙️ Adjust these values to suit your server's gameplay style!

---

## ✏️ Customization

* Use Minecraft color codes (e.g., `&a`, `&6`) in messages.
* Choose specific worlds where the plugin is active.
* Enable or disable visual and sound effects.
* Fine-tune delays and cooldowns for your preferred pacing.

---

## 🤝 Contributing

We welcome pull requests, bug reports, and feature suggestions!

1. Fork the repository.
2. Create your feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -m "Add new feature"`
4. Push to the branch: `git push origin feature/new-feature`
5. Open a Pull Request.

---

## 📞 Support

* [Issues Page](https://github.com/MRsuffixx/SinglePlayerSleep/issues)
* SpigotMC Discussion (if published)
* Discord server (if available)

If you encounter bugs, crashes, or need help configuring, please open an issue or join our Discord.

---

## 📃 License

Distributed under the MIT License.
See [LICENSE](LICENSE) for more information.

---

## 🌟 Thank you!

If you find this plugin useful, please consider giving it a ⭐ on GitHub and sharing it with other server owners.
Your feedback helps us improve and keep the project alive!

---

> 🧡 Built with love for the Minecraft community.

```
