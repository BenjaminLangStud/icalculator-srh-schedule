module icalCalculation.srh.schedule {
    requires ical4j.core;
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;

    opens com.benny.icalculation.gui to javafx.fxml;

    exports com.benny.icalculation.gui;
}