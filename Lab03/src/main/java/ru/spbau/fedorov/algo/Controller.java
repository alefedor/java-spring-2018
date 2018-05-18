package ru.spbau.fedorov.algo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.algo.client.FTPClient;
import ru.spbau.fedorov.algo.data.FileEntry;
import ru.spbau.fedorov.algo.server.FTPServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for FTP application
 */
public class Controller {

    @FXML
    private Button setHostnameButton;

    @FXML
    private TextField pathText;

    @FXML
    private TreeView<FileEntry> treeView;

    @FXML
    private TextField hostnameText = null;

    private String hostname;
    private volatile Thread server = null;
    private FTPClient client;

    /**
     * Initialization of GUI elements
     */
    public void initialize() {
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TreeItem<FileEntry> selected = treeView.getSelectionModel().getSelectedItem();

                if (selected == null || !selected.isLeaf() || event.getClickCount() != 2) {
                    return;
                }

                saveFile(null);
            }
        });
    }

    /**
     * Runs server on a local side
     */
    public void createServer(MouseEvent mouseEvent) {
        if (server != null) {
            alert("Already running");
            return;
        }

        server = new Thread(() -> {
            try {
                FTPServer.main(null);
            } catch (Exception e) {
                FTPServer.setRunning(false);
                Platform.runLater(() -> {
                    alert("Can't run server");
                    e.printStackTrace();
                    Platform.exit();
                });
            }
        });

        server.start();

        alert("Trying to start server (if no errors then successful)");
    }

    /**
     * Shuts down server if if runs on a local side.
     */
    public void shutdownServer(MouseEvent mouseEvent) {
        if (server == null) {
            alert("Server is off");
            return;
        }
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                while (FTPServer.isRunning()) {
                    server.interrupt();
                }
                return null;
            }
        };
        task.setOnSucceeded((event) -> {server = null; alert("Successful shutdown of server");});

        new Thread(task).start();
    }

    /**
     * Go to path denoted by pathText
     */
    public void walkToPath(MouseEvent mouseEvent) {
        if (!checkHostname()) {
            return;
        }

        String path = pathText.getText();
        try {
            List<FileEntry> entries = client.list(path);
            if (entries == null) {
                alert("No such directory found");
                return;
            }

            treeView.setRoot(new FTPTreeItem(new FileEntry(path, true), path));
        } catch (IOException e) {
            e.printStackTrace();
            panic();
        }
    }

    /**
     * Download file selected in a TreeView.
     */
    public void saveFile(MouseEvent mouseEvent) {
        if (!checkHostname()) {
            return;
        }

        TreeItem<FileEntry> selected = treeView.getSelectionModel().getSelectedItem();

        if (selected == null || !selected.isLeaf()) {
            alert("Select file");
            return;
        }


        String source = ((FTPTreeItem)selected).getPath();
        String currentDirectory = Paths.get(".").toAbsolutePath().toString();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(currentDirectory));
        fileChooser.setInitialFileName(new File(source).getName());
        Window window = treeView.getScene().getWindow();
        File destination = fileChooser.showSaveDialog(window);
        if (destination == null || destination.isDirectory()) {
            alert("Select destination");
            return;
        }

        alert("Download started");

        AtomicBoolean successful = new AtomicBoolean(true);

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    client.get(source, new FileOutputStream(destination));
                } catch (IOException e) {
                    successful.set(false);
                }
                return null;
            }
        };

        task.setOnSucceeded((event) -> {
            if (successful.get()) {
                alert("Download successful");
            } else {
                panic();
            }
        });

        new Thread(task).start();
    }

    /**
     * Informs a user.
     * @param info information to say.
     */
    private void alert(@NotNull String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    /**
     * Check whether hostname is chosen and correct
     * @return true if is correct
     */
    private boolean checkHostname() {
        if (hostname == null || hostname.equals("")) {
            alert("Set hostname");
            return false;
        }
        return true;
    }

    /**
     * Fixes hostname.
     */
    public void setHostname(MouseEvent mouseEvent) {
        hostname = hostnameText.getText();
        if (!checkHostname()) {
            return;
        }

        hostnameText.setDisable(true);
        setHostnameButton.setDisable(true);

        try {
            client = new FTPClient(hostname);
        } catch (IOException e) {
            e.printStackTrace();
            panic();
        }
    }

    /**
     * Shuts down application.
     */
    private void panic() {
        alert("Fail in IO. Exiting");
        Platform.exit();
    }

    /**
     * Class for FTP TreeView
     */
    private class FTPTreeItem extends TreeItem<FileEntry> {
        @Getter private final String path;
        private boolean loaded = false;

        /**
         * Constructor for FTPTreeItem
         * @param entry file to be denoted by FTPTreeItem
         * @param path path of the file
         */
        public FTPTreeItem(@NotNull FileEntry entry, @NotNull String path) {
            super(entry);
            this.path = path;
        }

        @Override
        public boolean isLeaf() {
            return !getValue().isDirectory();
        }

        @Override
        public ObservableList<TreeItem<FileEntry>> getChildren() {
            if (!loaded) {
                loaded = true;
                super.getChildren().setAll(loadChildren());
            }
            return super.getChildren();
        }

        /**
         * Download information about children of current directory
         * @return list of FTPTreeItems denoting children
         */
        private ObservableList<FTPTreeItem> loadChildren() {
            FileEntry entry = getValue();
            if (entry.isDirectory()) {
                try {
                    List<FileEntry> entries = client.list(path);

                    ObservableList<FTPTreeItem> children = FXCollections.observableArrayList();

                    for (FileEntry fileEntry : entries) {
                        String childPath = path;
                        if (!path.endsWith(File.separator)) {
                            childPath = childPath + File.separator;
                        }

                        childPath = childPath + fileEntry.getFilename();

                        children.add(new FTPTreeItem(fileEntry, childPath));
                    }

                    return children;
                } catch (IOException e) {
                    e.printStackTrace();
                    panic();
                }
            }

            return FXCollections.emptyObservableList();
        }
    }
}