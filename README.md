# RedStorm

## 🚀 Overview
The **RedStorm** is a distributed load-testing tool that allows a **master node** to coordinate multiple **slave nodes** to perform HTTP stress testing. It utilizes **Redis Pub/Sub** for task distribution and a **heartbeat system** to monitor active slaves.

## 🛠 Features
- **Distributed Load Testing** – Execute HTTP requests across multiple slave machines.
- **Master-Slave Architecture** – Master assigns tasks, slaves execute them.
- **Redis Pub/Sub** – Tasks are distributed in real time.
- **Heartbeat System** – Master tracks active slaves.
- **Randomized User-Agents** – Prevents easy detection.
- **Supports GET, POST, PUT, DELETE** requests.

---

## 🏗️ Project Structure
```
redstorm/
├── master/
│   ├── src/main/java/com/mrflyn/master/MasterMain.java
│   ├── build.gradle.kts
│   └── ...
├── slave/
│   ├── src/main/java/com/mrflyn/slave/SlaveMain.java
│   ├── src/main/java/com/mrflyn/slave/SlaveTaskListener.java
│   ├── build.gradle.kts
│   └── ...
├── redis/
│   ├── docker-compose.yml
│   ├── redis.conf
└── README.md
```

---

## 🚀 Setup & Installation

### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/mrflyn/redstorm.git
cd redstorm
```

### **2️⃣ Start Redis**
If you have Docker:
```sh
docker-compose up -d
```
Otherwise, install Redis and start it:
```sh
redis-server
```

### **3️⃣ Build the Master and Slave JARs**
```sh
./gradlew clean build
```

---

## 🖥️ Running the System

### **Run the Master**
```sh
java -jar master/build/libs/redstorm_master.jar
```
**Commands:**
- `slaves` → View active slaves.
- `exec` → Execute an HTTP stress test (asks for necessary details).
- `exit` → Stop the master.

### **Run Slave Instances**
```sh
java -jar slave/build/libs/redstorm_slave.jar
```
Run multiple slaves for distributed testing.

---

## 🎯 Usage

1. **Start the Master**
2. **Start multiple Slave instances**
3. **From Master, enter a task:**
   ```
   Enter target URL: http://example.com
   Enter HTTP method: GET
   Enter request body: (leave blank for GET requests)
   ```
4. **Slaves will execute the request immediately.**
5. **Type `slaves` to see active slaves.**
6. **Type `exec` to manually enter and distribute an HTTP stress test.**

---

## 🛠️ Technologies Used
- **Java** (Master & Slave)
- **Gradle (Kotlin DSL)**
- **Redis (Pub/Sub, Key-Value Store)**
- **OkHttp** (HTTP Requests)
- **Jedis** (Redis Client)
- **Docker** (Optional for Redis Deployment)

---

## 📜 License
MIT License © 2025 **MrFlyn (Dibyajyoti Dey)**

---

## 🤝 Contributing
1. **Fork the repository**
2. **Create a feature branch** (`feature/your-feature`)
3. **Commit your changes** (`git commit -m 'Added new feature'`)
4. **Push to GitHub** (`git push origin feature/your-feature`)
5. **Submit a Pull Request** 🚀

---

## 📝 Future Improvements
- 🌐 **Web UI for the Master**
- 📊 **Real-time Monitoring Dashboard**
- ⚡ **Custom Rate Limiting**
- 🔄 **Task Scheduling for Stress Tests**

---

### ⭐ **If you found this useful, give it a star on GitHub!** ⭐

