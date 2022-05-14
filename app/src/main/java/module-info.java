module mimg {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens mimg.viewer to javafx.fxml;
    exports mimg.viewer;
}
