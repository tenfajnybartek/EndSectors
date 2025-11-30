# EndSectors

**EndSectors** — experimental Minecraft sector framework for **Paper 1.24.1** with **MongoDB & Redis** 🗄️

One large Minecraft world, divided into multiple sectors on a single Paper server,  
with automatic player synchronization, seamless border teleportation, and global chat.

> ⚠️ WARNING: This project is **4fun** and **not suitable for production**.  
> Do not take it seriously – it is mainly developed for testing and experimentation.

---

## 🔹 Inspirations

The project is based on **PocketSectors** (Nukkit), rewritten in Java for Paper:  
- 🔗 [PocketSectors repo](https://github.com/ProjectCode-PL/PocketSectors/blob/master/nukkit/src/main/java/pl/projectcode/pocketsectors/nukkit/command/SectorCommand.java)

Some ideas are inspired by other public projects (just inspiration, **no code copying**):  
- 🔗 [OpenSectors](https://github.com/fajzu1/OpenSectors/tree/main/spigot/src/main/java/io/github/fajzu/sectors/bukkit)

---

## ⚙️ Requirements

- PaperMC 1.24.1  
- Redis  
- MongoDB  

---

## ✨ Features

- 🚪 **Smooth teleportation** between sectors on border crossing  
- 🔄 **Real-time player data synchronization** (inventory, enderchest, gamemode, fly status, etc.)  
- 💬 **Global chat** synchronized across all sectors  
- 🎯 **Advanced sector queue system** – players go to their last sector or a random one for load balancing  
- ⚡ **Plug-and-play** – configure YAML and teleportation/sync works out of the box

---

## 🛠️ Quick Start

1. Install **Paper 1.24.1**  
2. Configure **MongoDB** and **Redis** connection in `config.yml`  
3. Define your sectors in YAML  
4. Start the server and watch **EndSectors** handle teleportation, syncing, and queues automatically

---

## 🗺️ Example Map

- 🔗 [EndSectors Map](https://oski646.github.io/sectors-generator)

---

## ⚠️ Warnings

- YAML sector coordinates may cause slight teleporting **before the border**  
- Correct setup (matching frontend `sectors` array):  
  - Spawn sectors: `-250 / 250`  
  - Other sectors: `251 / 751` (or `-751 / -251` for negative axes)  
- Using old YAML (`250 / -250`) may produce weird border behavior  

---

## 📌 TODO

- Expand queue system  
- Sync optimization and bug fixes  
- Add new 4fun features
