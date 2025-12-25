package com.benny.icalculation.application;

import net.fortuna.ical4j.model.component.VEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainClassTest {

    @Test
    void parseEvent() {
        Exception exception = assertThrows(Exception.class, () -> {
            MainClass.parseEvent(new VEvent());
        });

        assertNotNull(exception);
    }
}