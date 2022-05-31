package mimg.viewer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageModel {
    private File currDir = new File(System.getProperty("user.dir"));
    private Image currImg = null;
    private final String imageRegex = "(.+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)";
    private final Stage stage;
    private boolean showHiddenFolders = false, showList = true;
    private final ImageView imageView = new ImageView();
    private final FileChooser fileChooser = new FileChooser();

    ImageModel(Stage stage) {
        this.stage = stage;
        
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.jpeg", "*.jpg", "*.png", "*.webp",
                        "*.bmp", "*.gif", "*.tiff", "*.tif", "*.JPG","*.JPEG", "*.PNG", "*.TIFF", "*.GIF", "*.TIF"),
                new FileChooser.ExtensionFilter(".jpg files", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG"),
                new FileChooser.ExtensionFilter(".png files", "*.PNG", "*.png"),
                new FileChooser.ExtensionFilter(".bmp files", "*.bmp"),
                new FileChooser.ExtensionFilter(".webp files", "*.webp"),
                new FileChooser.ExtensionFilter(".gif files", "*.gif"),
                new FileChooser.ExtensionFilter(".tiff files", "*.tif", "*.tiff", "*.TIF", "*.TIFF")
        );
        fileChooser.setTitle("Open Image");
    }

    void setCurrDir(String path) {
        try {
            currDir = new File(path);
            currImg = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    void setCurrImg(Image currImg) { this.currImg = currImg; }

    File getCurrDir() { return currDir; }

    Image getCurrImg() { return currImg; }

    String getPreviousImage(String[] images, int currImage) {
        int l = images.length;

        // below equation needed to make java modulo non-negative
        for (int i = (((currImage - 1) % l) + l) % l; i != currImage; i = (((i - 1) % l) + l) % l) {
            if (images[i].matches(imageRegex)) {
                return images[i];
            }
        }
        return null;
    }

    String getNextImage(String[] images, int currImage) {
        int l = images.length;

        // below equation needed to make java modulo non-negative
        for (int i = (((currImage + 1) % l) + l) % l; i != currImage; i = (((i + 1) % l) + l) % l) {
            if (images[i].matches(imageRegex)) {
                return images[i];
            }
        }
        return null;
    }

    double setHiddenList() {
        showList = !showList;

        if (showList) {
            return 0.15;
        } else {
            return 0;
        }
    }

    boolean setHiddenFolders() {
        showHiddenFolders = !showHiddenFolders;
        return showHiddenFolders;
    }

    File openImageFile() {
        return fileChooser.showOpenDialog(stage);
    }

    List<String> getCurrDirFiles() {
        List<String> currDirFiles = new ArrayList<>();

        if (getCurrDir().getParent() != null)
            currDirFiles.add("..");

        if (getCurrDir().list() == null) return currDirFiles;

        // item is an image or a directory (not hidden or showHiddenFolders)
        currDirFiles.addAll(List.of(Objects.requireNonNull(getCurrDir().list((dir, name) -> name.matches(imageRegex) ||
                (Files.isDirectory(Paths.get(dir + "/" + name))) && (!name.startsWith(".") || showHiddenFolders)))));

        return currDirFiles;
    }
}
