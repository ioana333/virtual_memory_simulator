package GUI;

import Model.*;
import Controller.*;
import Replacement.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class MemorySimulatorFrame extends JFrame {

    private JTextField pagesField;
    private JTextField framesField;
    private JComboBox<String> algorithmCombo;
    private JTextField sequenceField;

    private JButton initButton;
    private JButton stepButton;
    private JButton runAllButton;
    private JButton benchmarkButton;

    private JTable pageTable;
    private JTable frameTable;

    private JLabel currentPageLabel;
    private JLabel hitMissLabel;
    private JLabel victimLabel;
    private JLabel faultsLabel;

    private MemoryManager memoryManager;
    private PageTableModel pageTableModel;
    private FrameTableModel frameTableModel;

    private int[] accessSequence;
    private int currentIndex = 0;
    private Integer currentPage = 0;


    public MemorySimulatorFrame() {
        super("Virtual Memory Simulator");

        initComponents();
        layoutComponents();
        attachListeners();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // centreaza fereastra
    }

    private void initComponents() {
        pagesField = new JTextField("8", 5);
        framesField = new JTextField("3", 5);
        algorithmCombo = new JComboBox<>(new String[]{"FIFO", "LRU", "OPT"});
        sequenceField = new JTextField("0,2,1,0,3,2,4,0,2,3", 20);

        initButton = new JButton("Init");
        stepButton = new JButton("Next Step");
        runAllButton = new JButton("Run all");
        benchmarkButton = new JButton("Benchmark");

        runAllButton.setEnabled(false);
        stepButton.setEnabled(false);


        // tabele
        pageTableModel = new PageTableModel(null);
        frameTableModel = new FrameTableModel(null);

        pageTable = new JTable(pageTableModel);
        frameTable = new JTable(frameTableModel);

        currentPageLabel = new JLabel("Current Page: -");
        hitMissLabel = new JLabel("Result: -");
        victimLabel = new JLabel("Victim: -");
        faultsLabel = new JLabel("Page faults: 0");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Panel up
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Pages:"));
        topPanel.add(pagesField);
        topPanel.add(new JLabel("Frames:"));
        topPanel.add(framesField);
        topPanel.add(new JLabel("Algorithms:"));
        topPanel.add(algorithmCombo);
        topPanel.add(new JLabel("Access Sequence:"));
        topPanel.add(sequenceField);
        topPanel.add(initButton);
        topPanel.add(stepButton);
        topPanel.add(runAllButton);
        topPanel.add(benchmarkButton);

        add(topPanel, BorderLayout.NORTH);

        // Panel centru – tabele
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        JPanel pageTablePanel = new JPanel(new BorderLayout());
        pageTablePanel.add(new JLabel("Pages"), BorderLayout.NORTH);
        pageTablePanel.add(new JScrollPane(pageTable), BorderLayout.CENTER);

        JPanel frameTablePanel = new JPanel(new BorderLayout());
        frameTablePanel.add(new JLabel("RAM(frames)"), BorderLayout.NORTH);
        frameTablePanel.add(new JScrollPane(frameTable), BorderLayout.CENTER);

        centerPanel.add(pageTablePanel);
        centerPanel.add(frameTablePanel);

        add(centerPanel, BorderLayout.CENTER);

        // Panel jos – statistici
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2));
        bottomPanel.add(currentPageLabel);
        bottomPanel.add(hitMissLabel);
        bottomPanel.add(victimLabel);
        bottomPanel.add(faultsLabel);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.add(bottomPanel, BorderLayout.CENTER);
        southWrapper.setPreferredSize(new Dimension(0, 100));
        add(southWrapper, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        initButton.addActionListener(e -> initSimulation());
        stepButton.addActionListener(e -> doStep());
        runAllButton.addActionListener(e -> runAllSimulation());
        benchmarkButton.addActionListener(e -> runLargeBenchmark());
    }

    private void initSimulation() {
        try {
            int numberOfPages = Integer.parseInt(pagesField.getText().trim());
            int numberOfFrames = Integer.parseInt(framesField.getText().trim());

            // algoritm
            String algo = (String) algorithmCombo.getSelectedItem();
            ReplacementStrategy strategy;
            if ("LRU".equals(algo))
            {
                strategy = new LruReplacementStrategy();
            } else if ("FIFO".equals(algo))
            {
                strategy = new FifoReplacementStrategy();
            }
            else
            {
                strategy = new OptimalReplacementStrategy();
            }

            // secvența de acces
            String text = sequenceField.getText().trim();
            String[] parts = text.split(",");
            accessSequence = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                accessSequence[i] = Integer.parseInt(parts[i].trim());
            }

            currentIndex = 0;
            currentPage = null;

            //MemoryManager nou
            memoryManager = new MemoryManager(numberOfPages, numberOfFrames, strategy);

            //setez secventa daca opt
            if (strategy instanceof OptimalReplacementStrategy opt) {
                opt.setAccessSequence(accessSequence);
                opt.setNextIndex(currentIndex);
            }

            //noile referințe la modelele de tabel
            pageTableModel.setMemoryManager(memoryManager);
            frameTableModel.setMemoryManager(memoryManager);

            // reset etichete
            currentPageLabel.setText("Curent page: -");
            hitMissLabel.setText("Result: -");
            victimLabel.setText("Victim: -");
            faultsLabel.setText("Page faults: 0");

            stepButton.setEnabled(true);
            runAllButton.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Verify the input!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showFinalStatistics() {
        int total = memoryManager.getTotalAccesses();
        int faults = memoryManager.getPageFaultCount();
        int hits = memoryManager.getHitCount();
        double hitRate = memoryManager.getHitRate() * 100.0;
        double missRate = memoryManager.getMissRate() * 100.0;

        String algo = (String) algorithmCombo.getSelectedItem();
        int numberOfPages = Integer.parseInt(pagesField.getText().trim());
        int numberOfFrames = Integer.parseInt(framesField.getText().trim());

        String message = String.format(
                "End of access sequence.\n\n" +
                        "Algorithm: %s\n" +
                        "Pages: %d, Frames: %d\n\n" +
                        "Total accesses: %d\n" +
                        "Hits: %d (%.2f%%)\n" +
                        "Misses / Page faults: %d (%.2f%%)\n",
                algo, numberOfPages, numberOfFrames,
                total, hits, hitRate, faults, missRate
        );

        // scriere în fișier
        String logEntry = String.format(
                "=== Simulation run ===%n" +
                        "Algorithm: %s%n" +
                        "Pages: %d, Frames: %d%n" +
                        "Access sequence: %s%n" +
                        "Total accesses: %d%n" +
                        "Hits: %d (%.2f%%)%n" +
                        "Misses / Page faults: %d (%.2f%%)%n%n",
                algo,
                numberOfPages,
                numberOfFrames,
                sequenceField.getText().trim(),
                total,
                hits,
                hitRate,
                faults,
                missRate
        );

        try (PrintWriter out = new PrintWriter(new FileWriter("statistics.txt", true))) {
            out.print(logEntry);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Eroare la scrierea în fișier:\n" + ex.getMessage(),
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(this,
                message,
                "Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }


    private void runAllSimulation() {
        if (memoryManager == null || accessSequence == null) return;

        ReplacementStrategy rs = memoryManager.getReplacementStrategy();

        while (currentIndex < accessSequence.length) {
            int page = accessSequence[currentIndex];
            currentIndex++;

            currentPage = page;

            if (rs instanceof OptimalReplacementStrategy opt) {
                opt.setNextIndex(currentIndex);
            }

            memoryManager.accessPage(page);
        }

        pageTableModel.fireTableDataChanged();
        frameTableModel.fireTableDataChanged();

        showFinalStatistics();
        stepButton.setEnabled(false);
        runAllButton.setEnabled(false);
    }



    private void doStep() {
        if (memoryManager == null || accessSequence == null) {
            return;
        }

        if (currentIndex >= accessSequence.length) {
            showFinalStatistics();
            stepButton.setEnabled(false);
            runAllButton.setEnabled(false);
            return;
        }

        int page = accessSequence[currentIndex];
        currentIndex++;

        //ca sa stie opt "de unde incepe viitorul"
        ReplacementStrategy rs = memoryManager.getReplacementStrategy(); // vezi pasul 6
        if (rs instanceof OptimalReplacementStrategy opt) {
            opt.setNextIndex(currentIndex);
        }

        AccessResult result = memoryManager.accessPage(page);

        // actualizare tabelele
        pageTableModel.fireTableDataChanged();
        frameTableModel.fireTableDataChanged();

        // actualizare etichetele
        currentPageLabel.setText("Curent page: " + page);
        hitMissLabel.setText("Result: " + (result.isHit() ? "HIT" : "PAGE FAULT"));

        if (result.getVictimPage() == null) {
            victimLabel.setText("Victim: -");
        } else {
            victimLabel.setText("Victim: " + result.getVictimPage());
        }

        faultsLabel.setText("Page faults: " + result.getPageFaultCount());
    }

    private void runLargeBenchmark() {
        final int pages = 1_000;
        final int frames = 1_000;
        final int accesses = 2_000;
        final long seed = 123456789L;

        benchmarkButton.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                Result fifo = runOneResult("FIFO", new FifoReplacementStrategy(), pages, frames, accesses, seed);
                Result lru  = runOneResult("LRU",  new LruReplacementStrategy(),  pages, frames, accesses, seed);

                // winner: fewer faults, tie-break: faster time
                Result winner = fifo;
                if (lru.faults < winner.faults) {
                    winner = lru;
                } else if (lru.faults == winner.faults && lru.timeMs < winner.timeMs) {
                    winner = lru;
                }

                String table = buildBenchmarkTable(pages, frames, accesses, seed, fifo, lru, winner);

                File file = new File("benchmark.txt");
                try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
                    out.print(table);
                } catch (IOException ex) {
                    return "ERROR writing benchmark.txt: " + ex.getMessage();
                }

                return "DONE. Results saved to:\n" + file.getAbsolutePath();
            }

            @Override
            protected void done() {
                try {
                    String msg = get();
                    JOptionPane.showMessageDialog(
                            MemorySimulatorFrame.this,
                            msg,
                            "Benchmark",
                            msg.startsWith("ERROR") ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            MemorySimulatorFrame.this,
                            "Benchmark failed: " + ex.getMessage(),
                            "Benchmark",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    benchmarkButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private Result runOneResult(String name,
                                ReplacementStrategy strategy,
                                int pages, int frames, int accesses, long seed) {

        MemoryManager mm = new MemoryManager(pages, frames, strategy);
        Random rnd = new Random(seed); // IMPORTANT: same sequence for each algorithm

        long start = System.nanoTime();
        for (int i = 0; i < accesses; i++) {
            int page = rnd.nextInt(pages);
            mm.accessPage(page);
        }
        long timeMs = (System.nanoTime() - start) / 1_000_000;

        int faults = mm.getPageFaultCount();
        int hits = mm.getHitCount();
        double hitRate = mm.getHitRate() * 100.0;
        double missRate = mm.getMissRate() * 100.0;

        return new Result(name, hits, faults, hitRate, missRate, timeMs);
    }

    private String buildBenchmarkTable(int pages, int frames, int accesses, long seed,
                                       Result fifo, Result lru, Result winner) {

        StringBuilder sb = new StringBuilder();

        sb.append("=== BENCHMARK COMPARISON ===\n");
        sb.append(String.format("pages=%d  frames=%d  accesses=%d  seed=%d%n%n",
                pages, frames, accesses, seed));

        // Header (o singură dată)
        sb.append(String.format(
                "%-5s | %12s | %12s | %10s | %10s | %10s%n",
                "ALG", "HITS", "FAULTS", "HIT(%)", "MISS(%)", "TIME(ms)"
        ));
        sb.append("----------------------------------------------------------------------\n");

        // EXACT două rânduri: FIFO și LRU
        sb.append(formatRow(fifo));
        sb.append(formatRow(lru));

        sb.append("----------------------------------------------------------------------\n");

        // Comparație
        long diffFaults = (long) lru.faults - (long) fifo.faults; // semn: pozitiv => LRU are mai multe faults
        double diffHitRate = lru.hitRate - fifo.hitRate;         // semn: pozitiv => LRU are hitRate mai mare
        long diffTime = lru.timeMs - fifo.timeMs;                // semn: pozitiv => LRU e mai lent

        sb.append(String.format("WINNER: %s (fewest faults; tie -> fastest)%n", winner.name));
        sb.append(String.format(
                "COMPARE (LRU - FIFO): faults=%+d, hitRate=%+.2f%%, time=%+d ms%n",
                diffFaults, diffHitRate, diffTime
        ));
        sb.append("\n\n");

        return sb.toString();
    }


    private String formatRow(Result r) {
        return String.format(
                "%-6s | %12d | %12d | %10.2f | %10.2f | %10d%n",
                r.name, r.hits, r.faults, r.hitRate, r.missRate, r.timeMs
        );
    }

    // Helper class inside MemorySimulatorFrame (or as separate file)
    private static class Result {
        final String name;
        final int hits;
        final int faults;
        final double hitRate;
        final double missRate;
        final long timeMs;

        Result(String name, int hits, int faults, double hitRate, double missRate, long timeMs) {
            this.name = name;
            this.hits = hits;
            this.faults = faults;
            this.hitRate = hitRate;
            this.missRate = missRate;
            this.timeMs = timeMs;
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MemorySimulatorFrame frame = new MemorySimulatorFrame();
            frame.setVisible(true);
        });
    }
}
