package com.benny.icalculation.application.Caching;

import java.io.IOException;

public interface CacheService {
    String getData() throws IOException, InterruptedException;
}
