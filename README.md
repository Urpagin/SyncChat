# SyncChat
[
Also available on SpigotMC.
](https://www.spigotmc.org/resources/syncchat-discord-link.121376/)


SyncChat provides an interface between the chat functionality of a Minecraft server and a Discord channel.

**Everything is configurable** through the `plugin.yml` file. Nearly all text sent to either Minecraft or Discord can be tweaked in the config.

Here is an (old) demo (no slash-commands):

[Demo Video](https://github.com/Urpagin/DiscordLink/assets/72459611/ea6bf913-1dd4-4ba0-9f50-2040549207d3)

## 🏃‍♂️ Getting Started

> [!IMPORTANT]
> - Currently, SyncChat is built & tested for <ins>**Minecraft 1.21.4**</ins> (it may not work for prior versions)
> - SyncChat is built with the Spigot API and is compatible with Spigot and PaperMC servers onwards.

### 👍 Installation Steps

1. Download the `.jar` release file and place it in the `plugins` directory on your server.
2. Launch the server once to generate the config file at `./plugins/SyncChat/config.yml`.
3. Populate the `config.yml` with your Discord bot token and a channel ID.
4. Restart the server.
5. Enjoy!

## 🛠️ Additional Information

The plugin interacts with Discord through the [JDA](https://github.com/discord-jda/JDA) library.

## 📝 Todo

- [x] "Cannot reply to a system message" (e.g.: pinned messages)
- [x] Use Discord server nicknames in MC chat instead of the handle
- [x] Custom description?
- [x] Custom Rich Presence
- [x] Customize prefixes in `config.yml` (e.g.: [";", ":", "."])
- [x] Death logging in the Discord channel
- [x] `/playing` slashcommand
- [x] `/deaths` slashcommand
- [x] Version check at start: checks this repo for newer releases
- [x] MC & Discord message format customizable in config
- [x] `/playing` message format customizable in config
- [x] `/deaths` message format customizable in config
- [ ] Send a Discord message when a player joins (+ toggle in config)
- [ ] Send a Discord message when a player exits (+ toggle in config)
- [ ] Send a Discord message when a player earns an achievement (+ toggle in config)
- [ ] Change Discord bot username + icon to use the in-game name and skin face
- [ ] Update demo video

