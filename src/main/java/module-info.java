module com.olexyarm.jfxpnganalyzer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires java.desktop;

    opens com.olexyarm.jfxpnganalyzer to javafx.fxml;
    exports com.olexyarm.jfxpnganalyzer;
}
