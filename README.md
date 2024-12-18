# SyncChat

SyncChat provides an interface between the chat functionality of a Minecraft server and a Discord channel.

Here is a demo:

[Demo Video](https://github.com/Urpagin/DiscordLink/assets/72459611/ea6bf913-1dd4-4ba0-9f50-2040549207d3)

## 🏃‍♂️ Getting Started

SyncChat is built on top of Spigot and is compatible with PaperMC onwards. Currently, it is only available for Minecraft version 1.21.

### Installation Steps

1. Download the `.jar` release file and place it in the `plugins` directory on your server.
2. Launch the server once to generate the config file at `./plugins/SyncChat/config.yml`.
3. Populate the `config.yml` with your Discord bot token and a channel ID.
4. Restart the server.
5. Enjoy!

## 🛠️ Additional Information

The plugin interacts with Discord through the [JDA](https://github.com/discord-jda/JDA) library.

## 📝 Todo

- [x] "Cannot reply to a system message" (e.g.: pinned messages)
- [x] Use discord server nicknames in MC Chat, not handle
- [x] Custom description?
- [x] Custom Rich Presence
- [x] Customize prefixes in `config.yml` (e.g.: [";", ":", "."])
- [x] Death logging in the Discord channel
- [x] `/playing` slashcommand
- [x] `/deaths` slashcommand
- [ ] Version check at start: checks this repo for newer releases.
