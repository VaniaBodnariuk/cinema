package com.cinema.utility.file.basic.impl;

import com.cinema.utility.file.basic.FileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JacksonFileUtilityImpl<T> implements FileUtility<T> {
    private final File file;
    private final ObjectMapper objectMapper;
    private final Class<T> tClass;

    private JacksonFileUtilityImpl(Class<T> tClass,
                                   String fileName,
                                   String fileFormat,
                                   ObjectMapper objectMapper) {
        ValidatorUtility.validateFileFormat(fileFormat,fileName);
        this.file = Paths.get(fileName).toFile();
        this.objectMapper = objectMapper;
        this.tClass = tClass;
        configureObjectMapperForDateTimeWork(objectMapper);
    }

    public static <T> JacksonFileUtilityImpl<T> createInstanceForJson(
                                      Class<T> tClass, String fileName) {
        return new JacksonFileUtilityImpl<>(tClass, fileName, "json",
                                            new ObjectMapper());
    }

    public static <T> JacksonFileUtilityImpl<T> createInstanceForYaml(
                                      Class<T> tClass, String fileName) {
        return new JacksonFileUtilityImpl<>(tClass, fileName, "yaml",
                                            new ObjectMapper(
                                                    new YAMLFactory()));
    }

    @Override
    public List<T> read() throws IOException {
        CollectionType listType =
                objectMapper.getTypeFactory()
                            .constructCollectionType(ArrayList.class,
                                                     tClass);
        return objectMapper.readValue(file, listType);
    }

    @Override
    public void write(List<T> data) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file,data);
    }

    @Override
    public File getFile(){
        return file;
    }

    private void configureObjectMapperForDateTimeWork(
            ObjectMapper objectMapper){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }
}
