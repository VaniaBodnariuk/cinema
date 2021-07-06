package com.cinema.basic.interfaces;

import java.io.IOException;

public interface SaveToPersistenceApi {
    void synchronize() throws IOException;
}
