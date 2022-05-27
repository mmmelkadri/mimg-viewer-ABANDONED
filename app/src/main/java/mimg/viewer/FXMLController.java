package mimg.viewer;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FXMLController {
    @FXML private AnchorPane imageAnchor;
    @FXML private ImageView imageView;
    @FXML private ListView<String> listView;

    private File curr_dir = new File(System.getProperty("user.home"));
    private Image curr_img = null;

    public void initialize() {
        // TODO align image to center of imageAnchor
        imageView.fitWidthProperty().bind(imageAnchor.widthProperty());
        imageView.fitHeightProperty().bind(imageAnchor.heightProperty());

        setCanvas();
        setList();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (listView.getSelectionModel().getSelectedItem()
                    .matches("([^\\s]+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)")) {
                setImg(newValue);
            } else {
                setDir(curr_dir.toString() + "/" + newValue);
                listView.getItems().clear();
                listView.getSelectionModel().clearSelection();
                setList();
            }
            setCanvas();
        });
    }

    void setList() {
        // TODO fix problem if empty folder

        listView.getItems().add("..");

        listView.getItems().addAll(curr_dir.list((dir, name) -> name.matches("([^\\s]+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)") ||
                Files.isDirectory(Paths.get(dir + "/" + name))));
    }

    void setCanvas() {
        // display curr_img
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
