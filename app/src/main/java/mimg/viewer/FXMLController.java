package mimg.viewer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FXMLController {
    private final ImageView imageView = new ImageView();
    @FXML private AnchorPane imageAnchor;
    @FXML private GesturePane gesturePane;
    @FXML private ListView<String> listView;

    private File curr_dir = new File(System.getProperty("user.home"));
    private Image curr_img = null;

    public void initialize() {
        imageView.fitWidthProperty().bind(imageAnchor.widthProperty());
        imageView.fitHeightProperty().bind(imageAnchor.heightProperty());
        imageView.setPreserveRatio(true);

        gesturePane.setContent(imageView);

        setCanvas();
        setList();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // TODO understand why below line fixes bug
            if (newValue == null)
                return;

            if (listView.getSelectionModel().getSelectedItem()
                    .matches("([^\\s]+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)")) {
                setImg(newValue);
                setCanvas();
            } else {
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
        listView.getItems().add("..");
        listView.getItems().addAll(curr_dir.list((dir, name) -> name.matches("([^\\s]+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)") ||
                Files.isDirectory(Paths.get(dir + "/" + name))));
    }

    void setCanvas() {
        // TODO change zoom back to 100%
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
}
