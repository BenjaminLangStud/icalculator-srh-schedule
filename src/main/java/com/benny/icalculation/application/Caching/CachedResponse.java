package com.benny.icalculation.application.Caching;

import java.io.Serializable;
import java.time.Instant;

public class CachedResponse implements Serializable {
    public long timestamp;
    public String content;

    public CachedResponse() {}

    public CachedResponse(String content) {
        this.content = content;
        this.timestamp = Instant.now().getEpochSecond();
    }
}
