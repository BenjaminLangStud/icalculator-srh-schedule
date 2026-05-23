package com.example.benny.icalculation.core;

import com.example.benny.icalculation.core.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class ConfigTest {

    @Test
    void getInvalidateCacheAfterSeconds() {
        Assertions.assertEquals(Config.getInvalidateCacheAfterSeconds(), Config.getInvalidateCacheAfterSeconds());
    }
}