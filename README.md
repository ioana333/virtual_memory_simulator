# Virtual Memory Simulator (Java + JavaFX)

A visual **Virtual Memory Simulator** for teaching and experimentation. It models address translation and key OS mechanisms: page lookup, page-in/page-out, and page replacement. Compare multiple policies (FIFO, LRU, Optimal, Second-Chance/Clock, NRU), visualize state changes, and track metrics live.

---

##  Features

- **Page replacement policies**: FIFO, LRU, Optimal (Belady), Second-Chance/Clock, NRU (planned)
- **Simulation controls**: Run, Step, Pause, Reset
- **Configurable**: page size (logical), number of frames, policy, optional TLB (planned)
- **Live metrics**: page faults, hit rate, writes-back, access counters; CSV export (planned)
- **Trace support**: load from text/CSV; sample traces included
- **Cross-platform UI**: JavaFX (portable, lightweight)

---

##  What I learned

- How page faults occur and are resolved
- Trade-offs between FIFO / LRU / Optimal / Clock / NRU
- The effect of frame count and locality on fault rate
- (Optional) TLB impact on AMAT and hit/miss rates

---

## Getting Started

### Prerequisites
- Java 21 (Temurin or similar)
- Git
- No need to install Gradle; wrapper is included.

### Clone the repository and run the application
