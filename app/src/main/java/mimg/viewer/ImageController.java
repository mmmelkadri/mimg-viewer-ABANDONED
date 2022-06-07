package mimg.viewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;

public class ImageController {
    private final ImageView imageView = new ImageView();
    private Stage stage;
    ImageModel imageModel = new ImageModel(stage);
    @FXML private SplitPane splitPane;
    @FXML private AnchorPane imageAnchor;
    @FXML private MenuBar menuBar;
    @FXML private GesturePane gesturePane;
    @FXML private ListView<String> listView;

    @FXML
    private void setHidden(ActionEvent event) {
        if (imageModel.setHiddenFolders())
            ((MenuItem) event.getSource()).setText("Hide Hidden Folders");
        else
            ((MenuItem) event.getSource()).setText("Show Hidden Folders");

        setList();
    }

    @FXML
    void setHiddenList(ActionEvent event) {
        double position = imageModel.setHiddenList();

        if (position > 0) {
            ((MenuItem) event.getSource()).setText("Hide List");
            splitPane.setDividerPosition(0, position);
        } else {
            ((MenuItem) event.getSource()).setText("Show List");
            splitPane.setDividerPosition(0, position);
        }
    }

    // TODO fix rotate not filling the splitPane, zoom is also broken on rotated images
    // maybe zoom out gesturePane until entire image fits
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

        if (index < 0) return; // no selection made

        String previousImg = imageModel.getPreviousImage(listView.getItems().toArray(new String[0]), index);
        setImg(previousImg);
    }

    void nextImage() {
        int index = listView.getSelectionModel().getSelectedIndex();

        if (index < 0) return; // no selection made

        String nextImg = imageModel.getNextImage(listView.getItems().toArray(new String[0]), index);
        setImg(nextImg);
    }

    @FXML
    private void openImg() {
        File file = imageModel.openImageFile();
        imageModel.setCurrDir(file.getParent());
        setList();

        setImg(file.getName());
    }

    public void initialize() {
        imageView.fitWidthProperty().bind(imageAnchor.widthProperty());
        imageView.fitHeightProperty().bind(imageAnchor.heightProperty());

        imageView.setPreserveRatio(true);

        gesturePane.setContent(imageView);
        gesturePane.setHbarPolicy(GesturePane.ScrollBarPolicy.NEVER);

        menuBar.setUseSystemMenuBar(true);

        setList();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // TODO understand why line below fixes bug
            if (newValue == null) return;

            if (imageModel.isImg(listView.getSelectionModel().getSelectedItem())) {
                setImg(newValue);
            } else {
                if (newValue.equals(".."))
                    imageModel.setCurrDir(imageModel.getCurrDir().getParent());
                else
                    imageModel.setCurrDir(imageModel.getCurrDir().toString() + "/" + newValue);

                Platform.runLater(() -> {
                    listView.getSelectionModel().clearSelection();
                    setList();
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
                else if (!imageModel.isImg(item))
                    setTextFill(Color.ROYALBLUE);
                else
                    setTextFill(Color.BLACK);
            }
        });
    }

    void setStage (Stage stage) { this.stage = stage; }

    void setList() { listView.getItems().setAll(imageModel.getCurrDirFiles()); }

    void setImg(String name) {
        try {
            listView.getSelectionModel().select(name);
            imageModel.setCurrImg(new Image(imageModel.getCurrDir().toURI() + name));
            gesturePane.reset();
            imageView.setImage(imageModel.getCurrImg());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}