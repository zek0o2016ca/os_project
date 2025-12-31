# os_project

---

# Unified Page Replacement Simulator

A comprehensive **Java-based simulator** for analyzing and comparing classical **page replacement algorithms** used in virtual memory management.
The project focuses on **clarity, correctness, and educational value**, providing step-by-step execution and detailed statistics for each algorithm.

---

## 📌 Overview

This simulator allows running multiple page replacement algorithms on the **same reference string and frame size**, making it easy to compare their behavior, performance, and trade-offs.

It is designed primarily for:

* Computer Science students (Operating Systems)
* Educational demonstrations
* Conceptual understanding of memory management policies

---

## ✨ Features

* **Supported Algorithms**

  * FIFO (First-In, First-Out)
  * LRU (Least Recently Used)
  * LFU (Least Frequently Used)
  * MFU (Most Frequently Used)
  * Clock (Second Chance)

* **Unified Simulation Interface**

  * Same inputs for all algorithms
  * Consistent output format

* **Step-by-Step Visualization**

  * Memory frame state after each request
  * Page hit / page fault indication

* **Performance Metrics**

  * Total page requests
  * Page faults & hits
  * Fault rate (%)

* **Robust Input Validation**

  * Prevents invalid or inconsistent input
  * Clear error messages

---

## 🏗️ Project Structure

```
Unified-Page-Replacement/
├── src/
│   ├── FIFO/
│   │   └── FIFO_main.java
│   ├── LRU/
│   │   └── LRUCache.java
│   ├── LFU/
│   │   └── LFUPageReplacement.java
│   ├── MFU/
│   │   └── MFU_main.java
│   ├── Clock/
│   │   └── ClockManager.java
│   └── Main.java
└── README.md
```

Each algorithm is isolated in its own package to ensure **low coupling** and easy extensibility.

---

## 🚀 Getting Started

### Prerequisites

* Java Development Kit (JDK) 8 or higher
* Basic knowledge of virtual memory concepts

### Compilation

```bash
javac FIFO/*.java LRU/*.java LFU/*.java MFU/*.java Clock/*.java Main.java
```

### Run

```bash
java Main
```

---

## 📖 Usage Guide

### 1️⃣ Input Configuration

You will be prompted to enter:

* Number of frames (> 0)
* Number of page references (> 0)
* Page reference sequence (space or comma separated)

Example:

```
1 2 3 4 1 2 5 1 2 3 4 5
```

---

### 2️⃣ Algorithm Selection

```
Select Algorithm:
1. Clock (Second Chance)
2. LFU  (Least Frequently Used)
3. LRU  (Least Recently Used)
4. MFU  (Most Frequently Used)
5. FIFO (First-In, First-Out)
0. Exit
```

---

### 3️⃣ Output

For each algorithm, the simulator displays:

* Page request sequence
* Memory state after each request
* Page hit / page fault
* Final performance summary

---

## 🔍 Algorithm Descriptions

### FIFO (First-In, First-Out)

* Evicts the oldest page in memory
* Simple but vulnerable to **Belady’s anomaly**

### LRU (Least Recently Used)

* Evicts the page not used for the longest time
* Good practical performance with extra tracking overhead

### LFU (Least Frequently Used)

* Evicts the page with the lowest access frequency
* Works well with stable access patterns
* **Tie-breaking:** handled by insertion order

### MFU (Most Frequently Used)

* Evicts the page with the highest frequency
* Based on the assumption that frequently used pages may not be needed soon

### Clock (Second Chance)

* Approximation of LRU using reference bits
* Lower overhead than true LRU
* Uses a circular pointer (clock hand)

---

## 📊 Example Output

```
Requesting Page: 1
Result: PAGE FAULT
Frames: [1 | Bit: 1] [Empty | Bit: 0] [Empty | Bit: 0]

Requesting Page: 2
Result: PAGE FAULT
Frames: [1 | Bit: 1] [2 | Bit: 1] [Empty | Bit: 0]
```

### Final Summary

```
Total Requests: 12
Page Faults: 9
Fault Rate: 75.00%
```

---

## 🧪 Test Reference Strings

* **Basic test**

  ```
  1 2 3 1 4
  ```

* **Belady’s anomaly demonstration**

  ```
  1 2 3 4 1 2 5 1 2 3 4 5
  ```

* **Random workload**

  ```
  7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1
  ```

---

## 🧠 Design Decisions

* Each algorithm is implemented independently to simplify debugging and extension
* A unified menu ensures fair comparison across algorithms
* Priority was given to **educational clarity** over raw performance
* Output is verbose by design to help understand algorithm behavior

---

## ⚠️ General Observations (Not Guaranteed)

```
LRU ≈ Clock > FIFO > LFU > MFU
```

Performance heavily depends on access patterns and workload characteristics.

---

## 🛠️ Troubleshooting

* Ensure all packages are compiled together
* Page IDs should be non-negative integers
* Start with small inputs for easier verification

---

## 🎓 Educational Value

This project is suitable for:

* Operating Systems courses
* Algorithm comparison studies
* Classroom demonstrations
* Self-study and revision

---

## 🔄 Extending the Project

To add a new algorithm:

1. Create a new package
2. Implement the replacement logic
3. Add a menu option in `Main.java`
4. Follow the existing output format

---

## 📄 License

This project is intended for **educational use**.
Feel free to modify and extend it for learning or academic purposes.

---

**Happy simulating!** 🚀

---
