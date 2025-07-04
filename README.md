# NETIT (Verison: 1.1)
Network Emulation and Topology Implementation Tool
## Project Overview

This software allows users to quickly and intuitively design their own computer networks using a drag-and-drop interface, similar to tools like GNS3 or Cisco Packet Tracer. Unlike typical simulators, it generates a complete, practical deployment guide after the network topology is finalized, enabling users to move directly from design to real-world configuration.

Users can add routers, switches, servers, and workstations, select manufacturers and protocols, and configure services. Once the design is complete, the program analyzes the topology and creates a step-by-step tutorial with illustrations and explanations to help with actual network setup.


## Requirements and Building the Project
### Simple install

Just go to rpm folder and then:
```bash
 sudo dnf install ./netit-1.1-1.fc42.x86_64.rpm
 sudo yum install ./netit-1.1-1.fc42.x86_64.rpm
 sudo zypper install ./netit-1.1-1.fc42.x86_64.rpm
 sudo rpm -Uvh  netit-1.1-1.fc42.x86_64.rpm
```

### Requirements

* **JDK 21**
* **Maven** (version 3.6 or higher)
* **JavaFX SDK 21**

  * Downloaded and extracted (e.g., at `/opt/javafx-sdk-21/javafx-sdk-21.0.7/lib`)
* **Unix-like shell** (bash)

### Environment Setup

1. Set the environment variable pointing to the JavaFX `lib` folder if using a custom location:

   ```bash
   export JAVA_FX_SDK=/opt/javafx-sdk-21/javafx-sdk-21.0.7/lib
   ```
2. (Optional) Pass additional flags to Maven or Java via environment variables:

   ```bash
   export MAVEN_ARGS="-DskipTests"
   export JAVA_ARGS="--verbose"
   ```

### Build and Run

From the project root directory, run:

```bash
# 1) Clean previous build and build from scratch
./build.sh --clean

# 2) Run the application
./build.sh
```

The `build.sh` script will:

1. Clean `target/` and `out/` directories (when `--clean` is used).
2. Build the project with Maven (`mvn clean package`).
3. Copy the generated JAR to `out/netit.jar`.
4. Launch the JavaFX application with the correct `--module-path` and `--add-modules` options.


