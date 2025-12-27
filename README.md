<div align="center">
  <img src="https://i.imgur.com/pY0rpkr.jpeg" alt="EndSectors Banner" width="100%" />

  <h1>EndSectors</h1>

  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java" alt="Java 21" />
  <img src="https://img.shields.io/badge/Architecture-Microservices-blueviolet?style=for-the-badge" alt="Microservices" />
  <img src="https://img.shields.io/badge/License-Non--Commercial-red?style=for-the-badge" alt="License" />

  <br><br>

  <p>
    🎬 <b>See it in action:</b> <a href="https://www.youtube.com/watch?v=U_wk1nABo_M">YouTube Demo</a>
    &nbsp; | &nbsp;
    🗺️ <b>Interactive Map:</b> <a href="https://oski646.github.io/sectors-generator/">Sectors Generator</a>
  </p>
</div>

<hr>

**EndSectors** — experimental Minecraft sector framework for **Paper 1.24.1** with **NATS & Redis** 🗄️⚡

EndSectors allows you to run a single **Minecraft world** across multiple **Spigot servers**, each representing a **sector**.  
All sectors are connected via **Velocity**, giving players the feeling of one seamless world.

- Each sector has its own **boundaries** (default 10k per axis)
- Players can move seamlessly between sectors
- Player data (inventory, gamemode, enderchest, etc.) is synced in real-time via **Redis**
- Inter-server messaging and packets are handled by **NATS**
- **Common service** coordinates proxy and sectors, handles core logic, and ensures proper communication

> [!WARNING]
> This project is **experimental** and **not intended for production use**.  
> It is designed for testing and learning sector-based world mechanics.

---

## 🔹 Architecture

- **Common service** – central application that coordinates proxy and sector servers, handles core logic, and ensures proper communication
- **Velocity proxy** – connects all Spigot servers (sectors) together
- **Spigot sectors** – each run a part of the world
- **NATS** – handles messaging between sectors (packet system)
- **Redis** – stores and syncs player data

---

## 💬 Discord Support

Need help, want to chat, or follow updates? Join our **official Discord server**:  
👉 [dsc.gg/endsectors](https://dsc.gg/endsectors)  

Stay connected with the community and get real-time support.

---

## ⚙️ Requirements

- **Common service** – must be running before proxy or sectors!
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

1. **Start the Common service first** – EndSectors relies on it for proper operation.  
   Run it using:

   ```bash
   java -jar common-{VERSION}-BETA-all.jar
