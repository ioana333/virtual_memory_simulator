package ui;

import core.MemoryCore;
import domain.Frame;
import domain.PageRef;
import domain.SimStats;
import io.TraceLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import policy.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimulatorController {

    // UI
    private final TextField framesField = new TextField("3");
    private final TextField pageSizeField = new TextField("4096"); // opțional (informativ)
    private final ComboBox<String> policyBox = new ComboBox<>();
    private final Button loadBtn = new Button("Load Trace…");
    private final Button genBtn  = new Button("Generate Demo");
    private final Button runBtn  = new Button("Run");
    private final Button stepBtn = new Button("Step");
    private final Button pauseBtn= new Button("Pause");
    private final Button resetBtn= new Button("Reset");

    private final Label accLbl   = new Label("Acc: 0");
    private final Label faultLbl = new Label("Faults: 0");
    private final Label wrLbl    = new Label("WritesBack: 0");
    private final Label rateLbl  = new Label("FaultRate: 0.00");

    private final ListView<String> eventList = new ListView<>();
    private final GridPane framesGrid = new GridPane();

    // Chart
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number, Number> faultsChart = new LineChart<>(xAxis, yAxis);
    private final XYChart.Series<Number, Number> faultsSeries = new XYChart.Series<>();

    // Sim state
    private List<PageRef> trace = new ArrayList<>();
    private int cursor = 0;
    private MemoryCore core = null;
    private ReplacementPolicy policy = null;
    private Timeline loop = null;

    public Scene createScene() {
        // top controls
        policyBox.getItems().addAll("FIFO","LRU","CLOCK","OPT");
        policyBox.getSelectionModel().select("FIFO");
        framesField.setPrefColumnCount(4);
        pageSizeField.setPrefColumnCount(6);

        HBox ctrl1 = new HBox(8,
                new Label("Frames:"), framesField,
                new Label("PageSize:"), pageSizeField,
                new Label("Policy:"), policyBox,
                loadBtn, genBtn);
        ctrl1.setPadding(new Insets(10));

        HBox ctrl2 = new HBox(8, runBtn, stepBtn, pauseBtn, resetBtn,
                new Separator(), accLbl, faultLbl, wrLbl, rateLbl);
        ctrl2.setPadding(new Insets(10));

        // memory view
        framesGrid.setHgap(6); framesGrid.setVgap(6);
        framesGrid.setPadding(new Insets(10));
        BorderPane memoryPane = new BorderPane(framesGrid);
        memoryPane.setPadding(new Insets(10));
        memoryPane.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));

        // chart
        xAxis.setLabel("Accesses");
        yAxis.setLabel("Page Faults");
        faultsChart.setTitle("Faults over time");
        faultsChart.setCreateSymbols(false);
        faultsSeries.setName("Faults");
        faultsChart.getData().add(faultsSeries);

        SplitPane split = new SplitPane(memoryPane, eventList, faultsChart);
        split.setDividerPositions(0.33, 0.66);

        VBox root = new VBox(ctrl1, ctrl2, split);
        Scene sc = new Scene(root, 1024, 600);

        // wiring
        loadBtn.setOnAction(e -> onLoadTrace());
        genBtn.setOnAction(e -> onGenerateDemo());
        runBtn.setOnAction(e -> onRun());
        stepBtn.setOnAction(e -> onStep());
        pauseBtn.setOnAction(e -> onPause());
        resetBtn.setOnAction(e -> onReset());

        // auto loop
        loop = new Timeline(new KeyFrame(Duration.millis(200), e -> onStep()));
        loop.setCycleCount(Timeline.INDEFINITE);

        // init
        onReset();
        return sc;
    }

    // --- Actions ---
    private void onLoadTrace() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select trace file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv"));
        File f = fc.showOpenDialog(null);
        if (f == null) return;
        try {
            trace = TraceLoader.load(Path.of(f.toURI()));
            cursor = 0;
            eventList.getItems().add("Loaded trace: " + f.getName() + " (" + trace.size() + " refs)");
            refreshMetrics();
        } catch (Exception ex) {
            showError("Failed to load trace", ex);
        }
    }

    private void onGenerateDemo() {
        trace = new ArrayList<>();
        int[] seq = {1,2,3,2,4,1,2,5,2,1,6,2,3,2,4,1,2};
        for (int p : seq) trace.add(PageRef.read(p));
        cursor = 0;
        eventList.getItems().add("Generated demo trace (" + trace.size() + " refs)");
        refreshMetrics();
    }

    private void onRun() {
        if (loop.getStatus() != Timeline.Status.RUNNING) loop.play();
    }

    private void onPause() {
        if (loop.getStatus() == Timeline.Status.RUNNING) loop.stop();
    }

    private void onReset() {
        onPause();
        try {
            int frames = Math.max(1, Integer.parseInt(framesField.getText().trim()));
            policy = buildPolicy(policyBox.getValue(), frames);
            core = new MemoryCore(frames, policy);
            cursor = 0;
            eventList.getItems().add("Simulator reset (" + frames + " frames, policy=" + policyBox.getValue() + ")");
            rebuildFramesGrid(frames);
            faultsSeries.getData().clear();
            refreshMetrics();
        } catch (Exception ex) {
            showError("Reset failed", ex);
        }
    }

    private void onStep() {
        if (core == null || trace.isEmpty() || cursor >= trace.size()) {
            onPause();
            return;
        }
        PageRef ref = trace.get(cursor++);
        boolean hit = core.step(ref);
        eventList.getItems().add((hit ? "HIT " : "FAULT ") + "page " + ref.pageId() + (ref.write() ? " [W]" : ""));
        eventList.scrollTo(eventList.getItems().size()-1);
        refreshFramesGrid();
        refreshMetrics();
        if (cursor >= trace.size()) onPause();
    }

    private ReplacementPolicy buildPolicy(String name, int frameCount) {
        switch (name) {
            case "FIFO":  return new FifoPolicy();
            case "LRU":   return new LruPolicy();
            case "CLOCK": return new ClockPolicy(frameCount);
            case "OPT":
                List<Integer> future = trace.stream().map(PageRef::pageId).toList();
                return new OptimalPolicy(future);
            default: throw new IllegalArgumentException("Unknown policy: " + name);
        }
    }

    // --- UI helpers ---
    private void rebuildFramesGrid(int frames) {
        framesGrid.getChildren().clear();
        for (int i = 0; i < frames; i++) {
            var box = new VBox(2);
            box.setPadding(new Insets(6));
            box.setBorder(new Border(new BorderStroke(
                    Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            box.getChildren().addAll(new Label("Frame " + i), new Label("page=-"));
            framesGrid.add(box, i % 8, i / 8); // linii de câte 8
        }
    }

    private void refreshFramesGrid() {
        List<Frame> frames = core.getFrames();
        for (int i = 0; i < frames.size(); i++) {
            Frame fr = frames.get(i);
            VBox box = (VBox) framesGrid.getChildren().get(i);
            Label label = (Label) box.getChildren().get(1);
            if (fr.isValid()) {
                label.setText("page=" + fr.getPage() + (fr.isDirty() ? " *" : ""));
            } else {
                label.setText("page=-");
            }
        }
    }

    private void refreshMetrics() {
        SimStats s = core.stats();
        accLbl.setText("Acc: " + s.accesses);
        faultLbl.setText("Faults: " + s.pageFaults);
        wrLbl.setText("WritesBack: " + s.writesBack);
        rateLbl.setText(String.format("FaultRate: %.2f", s.faultRate()));
        if (s.accesses > 0) {
            faultsSeries.getData().add(new XYChart.Data<>(s.accesses, s.pageFaults));
        }
    }

    private void showError(String title, Exception ex) {
        ex.printStackTrace();
        new Alert(Alert.AlertType.ERROR, title + "\n" + ex.getMessage()).showAndWait();
    }
}
