package ru.spbau.fedorov.architectures;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Path;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.client.ClientRunner;
import ru.spbau.fedorov.architectures.protocol.ArchitectureTypes;
import ru.spbau.fedorov.architectures.protocol.ChangingParameter;
import ru.spbau.fedorov.architectures.util.TestInfo;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for FTP application
 */
public class Controller {

    @FXML private TextField iterationsField;
    @FXML private TextField pauseField;
    @FXML private TextField clientNumber;
    @FXML private TextField elemNumber;
    @FXML private TextField queryNumber;
    @FXML private ChoiceBox<String> parameterChoice;
    @FXML private TextField stepField;
    @FXML private ChoiceBox<String> architectureChoice;
    @FXML private LineChart<Integer, Double> clientChart;
    @FXML private LineChart<Integer, Double> clientQueryChart;
    @FXML private LineChart<Integer, Double> serverQueryChart;
    @FXML private Button setHostnameButton;

    @FXML private TextField hostnameText = null;

    private String hostname;

    private List<List<TestInfo>> graphInfo = new ArrayList<>();
    private int[] architectureTypes = new int[0];
    private int changingParameter;
    private int step;
    private int iterations;

    private void alert(@NotNull String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    private boolean checkHostname() {
        if (hostname == null || hostname.equals("")) {
            alert("Set hostname");
            return false;
        }
        return true;
    }

    public void setHostname(MouseEvent mouseEvent) {
        hostname = hostnameText.getText();
        if (!checkHostname()) {
            return;
        }

        hostnameText.setDisable(true);
        setHostnameButton.setDisable(true);
    }

    public void start(MouseEvent mouseEvent) {
        if (!checkHostname()) {
            return;
        }

        int pause = Integer.parseInt(pauseField.getText());
        int elements = Integer.parseInt(elemNumber.getText());
        int clients = Integer.parseInt(clientNumber.getText());
        int queries = Integer.parseInt(queryNumber.getText());
        step = Integer.parseInt(stepField.getText());
        iterations = Integer.parseInt(iterationsField.getText());

        switch (parameterChoice.getValue()) {
            case "Number of clients":
                changingParameter = ChangingParameter.CLIENT_NUMBER;
                break;

            case "Number of elements":
                changingParameter = ChangingParameter.ELEMENT_NUMBER;
                break;

            default:
                changingParameter = ChangingParameter.PAUSE;
        }

        graphInfo = new ArrayList<>();

        int architectures[];

        switch (architectureChoice.getValue()) {
            case "Separate thread server":
                architectures = new int[] {ArchitectureTypes.SEPARATE_THREAD};
                break;
            case "Thread pool server":
                architectures = new int[] {ArchitectureTypes.THREAD_POOL};
                break;
            case "Non blocking server":
                architectures = new int[] {ArchitectureTypes.NON_BLOCKING};
                break;
            default:
                architectures = new int[] {ArchitectureTypes.SEPARATE_THREAD,
                        ArchitectureTypes.THREAD_POOL, ArchitectureTypes.NON_BLOCKING};
        }

        architectureTypes = architectures;

        alert("Wait until graphs are built");

        new Thread(() -> {
            List<List<TestInfo>> result = new ArrayList<>();

            for (int architecture : architectures) {
                try {
                    List<TestInfo> list = ClientRunner.start(hostname, elements, clients, pause, queries,
                            changingParameter, step, iterations, architecture);
                    result.add(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        alert("Fail. Exiting");
                        Platform.exit();
                    });
                }
            }

            Platform.runLater(() -> {
                graphInfo = result;
                buildGraphs();
            });
        }).start();
    }

    private void buildGraphs() {
        @SuppressWarnings("unchecked")
        List<LineChart<Integer, Double>> charts = new ArrayList<>();
        charts.add(serverQueryChart);
        charts.add(clientChart);
        charts.add(clientQueryChart);

        for (int num = 0; num < 3; num++) {
            LineChart<Integer, Double> chart = charts.get(num);
            chart.getData().clear();
            chart.getXAxis().setLabel(getChangingParameterName(changingParameter));

            for (int i = 0; i < graphInfo.size(); i++) {
                List<TestInfo> tests = graphInfo.get(i);

                XYChart.Series<Integer, Double> series = new XYChart.Series<Integer, Double>();
                series.setName(getArchitectureName(i));
                for (TestInfo info : tests) {
                    series.getData().add(new XYChart.Data<>(getParamFromTestInfo(info, changingParameter),
                                                getTimeFromTestInfo(info, num)));
                }
                chart.getData().add(series);
            }
        }
    }

    private int getParamFromTestInfo(@NotNull TestInfo info, int changingParameter) {
        if (changingParameter == ChangingParameter.PAUSE) {
            return info.getPause();
        }
        if (changingParameter == ChangingParameter.ELEMENT_NUMBER) {
            return info.getElements();
        }

        return info.getClients();
    }

    private double getTimeFromTestInfo(@NotNull TestInfo info, int num) {
        if (num == 0) {
            return info.getAverageServerQuery();
        }

        if (num == 1) {
            return info.getAverageClient();
        }

        return info.getAverageClientQuery();
    }

    private String getArchitectureName(int architecture) {
        if (architecture == ArchitectureTypes.SEPARATE_THREAD) {
            return "Separate thread server";
        }
        if (architecture == ArchitectureTypes.THREAD_POOL) {
            return "Thread pool server";
        }

        return "Non blocking server";
    }

    private String getChangingParameterName(int changingParameter) {
        if (changingParameter == ChangingParameter.PAUSE) {
            return "Pause(ms)";
        }
        if (changingParameter == ChangingParameter.ELEMENT_NUMBER) {
            return "Number of elements";
        }

        return "Number of clients";
    }

    private String getSimpleChangingParameterName(int changingParameter) {
        if (changingParameter == ChangingParameter.PAUSE) {
            return "Pause";
        }
        if (changingParameter == ChangingParameter.ELEMENT_NUMBER) {
            return "Elements";
        }

        return "Clients";
    }

    public void save(MouseEvent mouseEvent) {
        String currentDirectory = Paths.get(".").toAbsolutePath().toString();
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(new File(currentDirectory));

        Window window = iterationsField.getScene().getWindow();
        File destination = dirChooser.showDialog(window);

        if (destination == null || !destination.isDirectory()) {
            alert("Select destination directory");
            return;
        }

        String path = destination.getAbsolutePath() + File.separator;
        saveChart(path, "QueryOnServer(" + getSimpleChangingParameterName(changingParameter) + ")", serverQueryChart);
        saveChart(path, "QueryOnClient(" + getSimpleChangingParameterName(changingParameter) + ")", clientQueryChart);
        saveChart(path, "Client(" + getSimpleChangingParameterName(changingParameter) + ")", clientChart);


        File file = new File(path + "Info(" +
                getSimpleChangingParameterName(changingParameter) + ").txt");
        try {
            file.createNewFile();
        } catch (Exception e){
            alert("Failed to save");
            return;
        }

        PrintWriter out;

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            out = new PrintWriter(outStream);
        } catch (Exception e) {
            alert("Failed to save");
            return;
        }

        for (int i = 0; i < graphInfo.size(); i++) {
            out.println(getArchitectureName(architectureTypes[i]));
            List<TestInfo> list = graphInfo.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (j == 0) {
                    out.println("Changing parameter: " + getChangingParameterName(changingParameter));
                    out.println("Step " + step);
                    out.println("Iterations " + iterations);
                    out.println("Clients " + list.get(j).getClients());
                    out.println("Queries " + list.get(j).getQueries());
                    out.println("Elements " + list.get(j).getElements());
                    out.println("Pause " + list.get(j).getPause());
                }
                out.println("Iteration number " + i);
                out.println("Average query on server " + list.get(j).getAverageServerQuery());
                out.println("Average query on client " + list.get(j).getAverageClientQuery());
                out.println("Average client time " + list.get(j).getAverageClient());
            }
            out.println("========================");
        }
        out.close();
    }

    public void saveChart(@NotNull String path, @NotNull String filename, @NotNull LineChart<Integer, Double> chart) {
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        File file = new File(path + filename + ".png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception e) {
            alert("Failed to save");
        }
    }
}
