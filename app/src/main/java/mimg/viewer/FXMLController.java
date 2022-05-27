package mimg.viewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FXMLController {
    private boolean showHiddenFolders = false;
    private final String imageRegex = "(.+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)";
    private final ImageView imageView = new ImageView();
    private Stage stage;
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
    private void openImg(ActionEvent event) {
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
            if (newValue == null)
                return;

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
    }

    void setList() {
        listView.getItems().clear();
        if (curr_dir.getParent() != null)
            listView.getItems().add("..");
        // item is an image or a directory (not hidden or showHiddenFolders)
        listView.getItems().addAll(curr_dir.list((dir, name) -> name.matches(imageRegex) ||
                (Files.isDirectory(Paths.get(dir + "/" + name))) && (!name.startsWith(".") || showHiddenFolders)));

        // TODO add more customization
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (item != null && !item.matches(imageRegex)) {
                            setTextFill(Color.ROYALBLUE);
                        }
                    }
                };
            }
        });
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