package GUI;

import Model.*;
import Controller.*;
import Replacement.*;

import javax.swing.*;
import java.awt.*;

public class MemorySimulatorFrame extends JFrame {

    private JTextField pagesField;
    private JTextField framesField;
    private JComboBox<String> algorithmCombo;
    private JTextField sequenceField;

    private JButton initButton;
    private JButton stepButton;

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
        algorithmCombo = new JComboBox<>(new String[]{"FIFO", "LRU"});
        sequenceField = new JTextField("0,2,1,0,3,2,4,0,2,3", 20);

        initButton = new JButton("Init");
        stepButton = new JButton("Next Step");
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

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        initButton.addActionListener(e -> initSimulation());
        stepButton.addActionListener(e -> doStep());
    }

    private void initSimulation() {
        try {
            int numberOfPages = Integer.parseInt(pagesField.getText().trim());
            int numberOfFrames = Integer.parseInt(framesField.getText().trim());

            // algoritm
            String algo = (String) algorithmCombo.getSelectedItem();
            ReplacementStrategy strategy;
            if ("LRU".equals(algo)) {
                strategy = new LruReplacementStrategy();
            } else {
                strategy = new FifoReplacementStrategy();
            }

            // secvența de acces
            String text = sequenceField.getText().trim();
            String[] parts = text.split(",");
            accessSequence = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                accessSequence[i] = Integer.parseInt(parts[i].trim());
            }
            currentIndex = 0;

            //MemoryManager nou
            memoryManager = new MemoryManager(numberOfPages, numberOfFrames, strategy);

            //noile referințe la modelele de tabel
            pageTableModel.setMemoryManager(memoryManager);
            frameTableModel.setMemoryManager(memoryManager);

            // reset etichete
            currentPageLabel.setText("Curent page: -");
            hitMissLabel.setText("Result: -");
            victimLabel.setText("Victim: -");
            faultsLabel.setText("Page faults: 0");

            stepButton.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Verify the input!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doStep() {
        if (memoryManager == null || accessSequence == null) {
            return;
        }

        if (currentIndex >= accessSequence.length) {
            JOptionPane.showMessageDialog(this,
                    "End of access sequence",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            stepButton.setEnabled(false);
            return;
        }

        int page = accessSequence[currentIndex];
        currentIndex++;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MemorySimulatorFrame frame = new MemorySimulatorFrame();
            frame.setVisible(true);
        });
    }
}
