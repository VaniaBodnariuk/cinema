package com.cinema.utility.file.csv.impl;

import com.cinema.exception.DataException;
import com.cinema.utility.file.csv.CsvFileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.Getter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

@Getter
public class OpenCSVCsvFileUtilityImpl<T> implements CsvFileUtility<T> {
    private File file;
    private final Class<T> tClass;

    public OpenCSVCsvFileUtilityImpl(Class<T> tClass, String fileName) {
        ValidatorUtility.validateFileFormat("csv",fileName);
        this.file = Paths.get(fileName).toFile();
        this.tClass = tClass;
    }

    @Override
    public List<T> read() {
        checkFileForExisting();
        if(getFile().length() == 0){
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file.toPath())) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(tClass)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void write(List<T> data) {
        try (Writer writer = Files.newBufferedWriter(file.toPath())) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(data);
        } catch (IOException | CsvRequiredFieldEmptyException
                             | CsvDataTypeMismatchException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    private void checkFileForExisting() {
        if(!file.canRead()){
            file = new File(file.getName());
        }
    }
}
