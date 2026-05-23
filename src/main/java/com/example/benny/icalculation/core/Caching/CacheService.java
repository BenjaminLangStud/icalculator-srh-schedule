package com.example.benny.icalculation.core.Caching;

import java.io.IOException;

public interface CacheService {
    String getData() throws IOException, InterruptedException;
}
