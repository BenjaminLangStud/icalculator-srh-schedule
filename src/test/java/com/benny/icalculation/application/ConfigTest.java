package com.benny.icalculation.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void getInvalidateCacheAfterSeconds() {
        Assertions.assertEquals(Config.getInvalidateCacheAfterSeconds(), Config.INVALIDATE_CACHE_AFTER_SECONDS);
    }
}