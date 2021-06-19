package com.cinema.utility.file.jacksonimpl;

import com.cinema.utility.file.FileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JacksonFileUtilityImpl<T> implements FileUtility<T> {
    @Getter
    private final File file;
    private final ObjectMapper objectMapper;
    private final Class<T> tClass;

    private JacksonFileUtilityImpl(Class<T> tClass,
                                   String fileName,
                                   ObjectMapper objectMapper) {
        ValidatorUtility.validateFileFormat("json",fileName);
        this.file = Paths.get(fileName).toFile();
        this.objectMapper = objectMapper;
        this.tClass = tClass;
        configureObjectMapperForDateTimeWork(objectMapper);
    }

    private void configureObjectMapperForDateTimeWork(
                             ObjectMapper objectMapper){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    public JacksonFileUtilityImpl<T> createInstanceForJson(String fileName) {
        ValidatorUtility.validateFileFormat("json",fileName);
        return new JacksonFileUtilityImpl<>(tClass, fileName, new ObjectMapper());
    }

    public JacksonFileUtilityImpl<T> createInstanceForYaml(String fileName) {
        ValidatorUtility.validateFileFormat("yaml",fileName);
        return new JacksonFileUtilityImpl<>(tClass,fileName,
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
}
