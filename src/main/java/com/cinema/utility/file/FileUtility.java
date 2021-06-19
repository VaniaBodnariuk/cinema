package com.cinema.utility.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileUtility<T> {
    List<T> read() throws IOException;

    void write(List<T> data) throws IOException;

    File getFile();
}
