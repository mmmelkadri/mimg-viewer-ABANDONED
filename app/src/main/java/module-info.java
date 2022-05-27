module mimg {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.kurobako.gesturefx;

    opens mimg.viewer to javafx.fxml;
    exports mimg.viewer;
}
