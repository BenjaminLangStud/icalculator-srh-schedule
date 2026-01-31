module com.example.benny.icalculation.core {
    requires ical4j.core;
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires atlantafx.base;
    requires java.desktop;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.apache.commons.lang3;

    opens com.example.benny.icalculation.gui to javafx.fxml;

    exports com.example.benny.icalculation.core;
    exports com.example.benny.icalculation.excel;
}