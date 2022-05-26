package mimg.viewer;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class FXMLController {
    @FXML private ImageView imageView;
    @FXML private ListView<String> listView;

    private File curr_dir = new File(System.getProperty("user.home"));
    private Image curr_img = null;

    public void initialize() {
        setCanvas();
        setList();
    }

    void setList() {
        // TODO add parent directory as .. to dir_list
        // TODO fix bug with isDirectory check
        listView.getItems().addAll(curr_dir.list((dir, name) -> name.matches("([^\\s]+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)") ||
                        new File(dir + name).isDirectory()));

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (listView.getSelectionModel().getSelectedItem()
                    .matches("([^\\s]+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)")) {
                setImg(newValue);
            } else {
                setDir(curr_dir + "/" + newValue);
            }
            // TODO add option for .. to go to parent dir if it exists

            setList();
            setCanvas();
        });
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
            curr_img = new Image(curr_dir + "/" + name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
