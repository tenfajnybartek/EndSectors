# EndSectors

**EndSectors** — experimental Minecraft sector framework for **Paper 1.24.1** with **NATS & Redis** 🗄️⚡

EndSectors allows you to run a single **Minecraft world** across multiple **Spigot servers**, each representing a **sector**.  
All sectors are connected via **Velocity**, giving players the feeling of one seamless world.  

- Each sector has its own **boundaries** (default 10k per axis)  
- Players can move seamlessly between sectors  
- Player data (inventory, gamemode, enderchest, etc.) is synced in real-time via **Redis**  
- Inter-server messaging and packets are handled by **NATS**  

🎬 **See it in action:** [YouTube Demo](https://www.youtube.com/watch?v=U_wk1nABo_M)  
Check out an **interactive sector map example**: [Sectors Generator](https://oski646.github.io/sectors-generator/)

> [!WARNING]
> This project is **experimental** and **not intended for production use**.  
> It is designed for testing and learning sector-based world mechanics.

---

## 🔹 Architecture

- **Velocity proxy** connects all Spigot servers (sectors) together  
- **Spigot sectors** each run a part of the world  
- **NATS** handles messaging between sectors (packet system)  
- **Redis** stores and syncs player data (no longer used for messaging)

---

## ⚙️ Requirements

- PaperMC / Spigot 1.20+ (tested on 1.24.1)  
- Velocity proxy  
- Redis for player data caching  
- NATS server for messaging  

---

## ✨ Features

- 🚪 **Seamless teleportation** between sectors  
- 🔄 **Real-time player data sync** across sectors  
- 💬 **Global chat** across all sectors  
- 🎯 **Sector queue system** – handles load balancing and last known sector for players  
- ⚡ **Plug-and-play** – configure JSON and sector management is automatic  

---

## 🛠️ Quick Start

1. Install **Paper 1.20+** on each sector server  
2. Set up **Velocity** as the proxy  
3. Configure **Redis** for player data  
4. Start a **NATS server** for messaging  
5. Define sectors in JSON and launch – EndSectors handles syncing, teleportation, and queues automatically  

---

## 📌 TODO

- Improve messaging reliability between sectors  
- Optimize performance for large player counts  
- Add optional experimental features for sector behavior  
