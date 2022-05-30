package mimg.viewer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.kurobako.gesturefx.GesturePane;

public class ImageModel {
    private final String imageRegex = "(.+(\\.(?i)(jpe?g|png|webp|bmp|gif|tiff))$)";
    private boolean showHiddenFolders = false, showList = true;
    private final ImageView imageView = new ImageView();

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
}
