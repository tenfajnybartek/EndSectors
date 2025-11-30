# EndSectors

**EndSectors** — experimental Minecraft sector framework for **Paper 1.24.1** with **MongoDB & Redis** 🗄️

EndSectors allows you to split a single large Minecraft world into multiple **sectors** on one Paper server.  
Players can move seamlessly between sectors, chat globally, and have their data synced in real-time.  

> [!WARNING]
> This project is **4fun** and **not suitable for production**.  
> Don’t take it seriously – it’s mainly for testing, learning, and experimenting with sector-based worlds.

---
## 🔹 What is this?

- EndSectors is a **fork of PocketSectors (Nukkit)**, rewritten in Java for Paper/Spigot with many changes and improvements.  
- This is **not a 1:1 copy** of PocketSectors – many parts were modified to fit Paper/Spigot, architecture changed, and new features added.  
- Some ideas were inspired by other public projects (concepts only, **no code copied**).  
- **Redis integration** is directly inspired by PocketSectors (2019) and slightly modified for Paper (Java) to fit EndSectors’ architecture.  
- No code, classes, or implementation from OpenSectors, OpenSourceSectors, or OpenSectors original were used; all modifications are based solely on PocketSectors concepts.  

Basically: this is a **learning/testing framework**, done for fun. Not meant for serious production servers.





---

## 🔹 Inspirations

- **PocketSectors (Nukkit)** — base project to understand sector mechanics:  
  - 🔗 [PocketSectors repo](https://github.com/ProjectCode-PL/PocketSectors/blob/master/nukkit/src/main/java/pl/projectcode/pocketsectors/nukkit/command/SectorCommand.java)

- Other public projects (concept inspiration, no code copying):  
  - 🔗 [OpenSectors](https://github.com/fajzu1/OpenSectors/tree/main/spigot/src/main/java/io/github/fajzu/sectors/bukkit)  
  - 🔗 [OpenSectors original](https://github.com/SocketByte/OpenSectors)  
  - 🔗 [OpenSourceSectors](https://github.com/Inder00/OpenSourceSectors/tree/main/Spigot)


---

## ⚙️ Requirements

- PaperMC 1.24.1  
- Redis (for sector sync)  
- MongoDB  

---

## ✨ Features

- 🚪 **Smooth teleportation** between sectors on border crossing  
- 🔄 **Real-time player data sync** (inventory, enderchest, gamemode, fly status, etc.)  
- 💬 **Global chat** synchronized across all sectors  
- 🎯 **Advanced sector queue system** – players go to their last sector or a random one for load balancing  
- ⚡ **Plug-and-play** – configure JSON, and teleportation/sync works automatically

---

## 🛠️ Quick Start

1. Install **Paper 1.24.1**  
2. Configure **MongoDB** and **Redis** in `config.json`  
3. Define your sectors in JSON  
4. Start the server and watch **EndSectors** handle teleportation, syncing, and queues automatically

---

## 🗺️ Example Map

- 🔗 [EndSectors Map](https://oski646.github.io/sectors-generator)

---

## ⚠️ Warnings

- JSON sector coordinates may cause slight teleporting **before the border**  
- Correct setup (matching frontend `sectors` array):  
  - Spawn sectors: `-250 / 250`  
  - Other sectors: `251 / 751` (or `-751 / -251` for negative axes)  
- Using old coordinates (`250 / -250`) may produce weird border behavior  

---

## 📌 TODO

- Expand and improve the queue system (handling larger player counts more efficiently)  
- Optimize sync and fix potential bugs (especially when multiple players interact with sectors simultaneously)  
- Add more fun/optional features  

> ⚠️ Note: Some synchronization issues may occur with many players at once. These are known and will be fixed in future updates.

