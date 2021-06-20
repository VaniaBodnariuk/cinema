package com.cinema.utility.file.csv.impl;

import com.cinema.utility.file.csv.CsvFileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.SneakyThrows;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

public class OpenCSVCsvFileUtilityImpl<T> implements CsvFileUtility<T> {
    private final Path path;
    private final Class<T> tClass;

    public OpenCSVCsvFileUtilityImpl(Class<T> tClass, String fileName) {
        ValidatorUtility.validateFileFormat("csv",fileName);
        this.path = Paths.get(fileName);
        this.tClass = tClass;
    }

    @Override
    public List<T> read() throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(tClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }

    //TODO Deal with SneakyThrows
    @SneakyThrows(value = {CsvDataTypeMismatchException.class,
                           CsvRequiredFieldEmptyException.class})
    @Override
    public void write(List<T> data) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(data);
        }
    }

    @Override
    public File getFile() {
        return path.toFile();
    }
}
