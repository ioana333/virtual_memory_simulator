# Virtual Memory Simulator (Paging)

A Java-based educational simulator for **virtual memory paging**, designed to illustrate how pages are loaded into physical memory (RAM), how **page faults** occur, and how different **page replacement algorithms** behave.

The project includes a **Swing graphical interface**, step-by-step execution, full simulation mode, and a **benchmarking system** for comparing algorithms.

## Project Overview

Modern operating systems use **virtual memory** to give processes the illusion of a large, contiguous address space. This simulator models the paging mechanism by explicitly representing:

- Virtual pages
- Physical frames (RAM)
- Page table
- Page replacement algorithms

The simulator focuses on understanding **behavior**, not hardware-level timing.

## Implemented Algorithms

| Algorithm | Description |
|---------|------------|
| FIFO | Replaces the page that has been in memory the longest |
| LRU | Replaces the least recently used page |
| OPT | Replaces the page whose next use is farthest in the future (theoretical optimum) |

## 🖥️ Graphical Interface (Swing)

The GUI allows the user to:

- Set number of pages and frames
- Choose the replacement algorithm (FIFO / LRU / OPT)
- Enter or generate an access sequence
- Run the simulation:
  - **Step-by-step**
  - **Run all**
- Visualize:
  - Page table
  - Physical memory frames
  - Current page, hit/miss status, victim page
- Export statistics and benchmarks to text files

---

## How to Run

### From IntelliJ IDEA
1. Open the project
2. Run `MemorySimulatorFrame` (GUI)
3. Use the interface to configure and run simulations

### From Console (optional)
Run `Simulator.java` for a non-GUI test run.

---

## Notes

- The OPT algorithm is implemented for **educational purposes** only, as real operating systems cannot know future memory accesses.
- High miss rates are expected when access patterns are random and physical memory is small relative to virtual memory.


