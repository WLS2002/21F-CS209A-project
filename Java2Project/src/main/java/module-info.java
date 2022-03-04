module com.example.java2project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires eu.hansolo.tilesfx;
    requires java.datatransfer;
    requires java.desktop;


    opens com.example.java2project to javafx.fxml;
    exports com.example.java2project;
    exports com.example.java2project.test;
    opens com.example.java2project.test to javafx.fxml;
}