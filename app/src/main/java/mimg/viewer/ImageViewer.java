/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mimg.viewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ImageViewer extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene.fxml"));
            Parent root = loader.load();
            ImageController controller = loader.getController();
            controller.setStage(stage);

            // TODO if opened with image, set image as curr_img
            Rectangle2D screen = Screen.getPrimary().getBounds();
            Scene scene = new Scene(root, screen.getWidth() * 0.9, screen.getHeight() * 0.8);
            // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            stage.setFullScreenExitHint("Press F or ESC to exit full-screen mode.");
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode().equals(KeyCode.LEFT))
                    controller.previousImage();
                else if (event.getCode().equals(KeyCode.RIGHT))
                    controller.nextImage();
                else if (event.getCode().equals(KeyCode.F))
                    stage.setFullScreen(!stage.isFullScreen());
            });

            stage.setTitle("mimg viewer");
            // TODO set icon
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}