package mimg.viewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FXMLController {
    private final String imageRegex = "(.+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)";
    private boolean showHiddenFolders = false, showList = true;
    private final ImageView imageView = new ImageView();
    private Stage stage;
    @FXML private SplitPane splitPane;
    @FXML private AnchorPane imageAnchor;
    @FXML private MenuBar menuBar;
    @FXML private GesturePane gesturePane;
    @FXML private ListView<String> listView;

    private File curr_dir = new File(System.getProperty("user.dir"));
    private Image curr_img = null;

    @FXML
    private void setHidden(ActionEvent event) {
        showHiddenFolders = !showHiddenFolders;

        if (showHiddenFolders)
            ((MenuItem) event.getSource()).setText("Hide Hidden Folders");
        else
            ((MenuItem) event.getSource()).setText("Show Hidden Folders");

        setList();
    }

    @FXML
    void setHiddenList(ActionEvent event) {
        showList = !showList;
        // TODO find better solution than setPrefWidth
        if (showList) {
            ((MenuItem) event.getSource()).setText("Hide List");
            splitPane.setDividerPosition(0, 0.15);
        }
        else {
            ((MenuItem) event.getSource()).setText("Show List");
            splitPane.setDividerPosition(0, 0);
        }
    }

    @FXML

    void rotateLeft() {
        gesturePane.setRotate(gesturePane.getRotate() - 90);
    }

    @FXML
    void rotateRight() {
        gesturePane.setRotate(gesturePane.getRotate() + 90);
    }

    void previousImage() {
        int index = listView.getSelectionModel().getSelectedIndex();
        String[] items = listView.getItems().toArray(new String[0]);
        int l = items.length;

        if (index < 0) return; // no selection made

        // below equation needed to make java modulo non-negative
        for (int i = (((index - 1) % l) + l) % l; i != index; i = (((i - 1) % l) + l) % l) {
            if (items[i].matches(imageRegex)) {
                listView.getSelectionModel().select(i);
                setImg(items[i]);
                setCanvas();
                return;
            }
        }
    }

    void nextImage() {
        int index = listView.getSelectionModel().getSelectedIndex();
        String[] items = listView.getItems().toArray(new String[0]);
        int l = items.length;

        if (index < 0) return; // no selection made

        // below equation needed to make java modulo non-negative
        for (int i = (((index + 1) % l) + l) % l; i != index; i = (((i + 1) % l) + l) % l) {
            if (items[i].matches(imageRegex)) {
                listView.getSelectionModel().select(i);
                setImg(items[i]);
                setCanvas();
                return;
            }
        }
    }

    @FXML
    private void openImg() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.jpeg", "*.jpg", "*.png", "*.webp",
                        "*.bmp", "*.gif", "*.tiff", "*.tif", "*.JPG","*.JPEG", "*.PNG", "*.TIFF", "*.TIF"),
                new FileChooser.ExtensionFilter(".jpg files", "*.jpg", "*.jpeg",
                        "*.JPG", "*.JPEG"),
                new FileChooser.ExtensionFilter(".png files", "*.PNG", "*.png"),
                new FileChooser.ExtensionFilter(".bmp files", "*.bmp"),
                new FileChooser.ExtensionFilter(".webp files", "*.webp"),
                new FileChooser.ExtensionFilter(".gif files", "*.gif"),
                new FileChooser.ExtensionFilter(".tiff files", "*.tif", "*.tiff",
                        "*.TIF", "*.TIFF")
        );

        File file = fileChooser.showOpenDialog(stage);
        setDir(file.getParent());
        setImg(file.getName());

        setCanvas();
        setList();

        listView.getSelectionModel().select(file.getName());
    }

    public void initialize() {
        imageView.fitWidthProperty().bind(imageAnchor.widthProperty());
        imageView.fitHeightProperty().bind(imageAnchor.heightProperty());
        imageView.setPreserveRatio(true);
        gesturePane.setContent(imageView);

        menuBar.setUseSystemMenuBar(true);

        setCanvas();
        setList();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // TODO understand why line below fixes bug
            if (newValue == null) return;

            if (listView.getSelectionModel().getSelectedItem()
                    .matches(imageRegex)) {
                setImg(newValue);
                setCanvas();
            } else {
                if (newValue.equals(".."))
                    setDir(curr_dir.getParent());
                else
                    setDir(curr_dir.toString() + "/" + newValue);

                Platform.runLater(() -> {
                    listView.getSelectionModel().clearSelection();
                    setList();
                    setCanvas();
                });
            }
        });

        // TODO add more customization
        listView.setCellFactory(cellData -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null)
                    setGraphic(null);
                else if (!item.matches(imageRegex))
                    setTextFill(Color.ROYALBLUE);
                else
                    setTextFill(Color.BLACK);
            }
        });
    }

    void setList() {
        listView.getItems().clear();
        if (curr_dir.getParent() != null)
            listView.getItems().add("..");

        if (curr_dir.list() == null) return; // prevents errors with network volumes

        // item is an image or a directory (not hidden or showHiddenFolders)
        listView.getItems().addAll(curr_dir.list((dir, name) -> name.matches(imageRegex) ||
                (Files.isDirectory(Paths.get(dir + "/" + name))) && (!name.startsWith(".") || showHiddenFolders)));
    }

    void setCanvas() {
        gesturePane.reset();
        imageView.setImage(curr_img);
    }

    void setDir(String path) {
        try {
            curr_dir = new File(path);
            curr_img = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    void setImg(String name) {
        try {
            curr_img = new Image(curr_dir.toURI() + name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }
}