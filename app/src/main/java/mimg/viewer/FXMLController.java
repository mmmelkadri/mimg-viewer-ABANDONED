package mimg.viewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FXMLController {
    private boolean showHiddenFolders = false;
    private final String imageRegex = "(.+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)";
    private final ImageView imageView = new ImageView();
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
}