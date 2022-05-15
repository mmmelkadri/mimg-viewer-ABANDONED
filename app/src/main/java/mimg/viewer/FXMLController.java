package mimg.viewer;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class FXMLController {
    private File curr_dir = new File(System.getProperty("user.home"));
    private Image curr_img = null;

    // left pane has list of all img files in current directory
        // list all img files and folders including .. for parent
        // if click on image
            // change curr_img
            // set curr_img icon to blue
        // if click on folder
            // change curr_dir
            // change curr_img to null
        // refresh img and left panel

    // right pane displays image
        // display curr_img

    void setDir(String path) {
        if (path == null)
            return;
        curr_dir = new File(path);
    }

    void setImg(String name) {
        try {
            curr_img = new Image(name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {

    }
}
