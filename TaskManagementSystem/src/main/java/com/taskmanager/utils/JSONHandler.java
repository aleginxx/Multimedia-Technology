package com.taskmanager.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JSONHandler {

//    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        return objectMapper;
    }

    public static <T> List<T> readListFromFile(String filePath, TypeReference<List<T>> typeRef) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            objectMapper().writeValue(file, List.of()); 
        }
        return objectMapper().readValue(file, typeRef);
    }

    public static <T> void writeListToFile(String filePath, List<T> data) throws IOException {
        objectMapper().writeValue(new File(filePath), data);
    }

    public static <T> T readObjectFromFile(String filePath, Class<T> clazz) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }
        return objectMapper().readValue(file, clazz);
    }

    public static <T> void writeObjectToFile(String filePath, T data) throws IOException {
        objectMapper().writeValue(new File(filePath), data);
    }
}