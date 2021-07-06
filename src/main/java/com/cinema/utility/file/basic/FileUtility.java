package com.cinema.utility.file.basic;

import java.io.File;
import java.util.List;

public interface FileUtility<T> {
    List<T> read();

    void write(List<T> data);

    File getFile();
}
