package com.cinema.repository.ticket.impl;

import com.cinema.exception.NotFoundException;
import com.cinema.model.Ticket;
import com.cinema.repository.ticket.TicketFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

//TODO Include FileUtility and format code
public class TicketFileRepositoryImpl implements TicketFileRepository {
    private final File file;
    private final Map<UUID, Ticket> localStorage;

    public TicketFileRepositoryImpl(File file) throws IOException {
        this.file = file;
        this.localStorage = initLocalStorage();
    }

    private Map<UUID, Ticket> initLocalStorage() throws IOException {
        if(file.length() == 0){
            return new HashMap<>();
        }
        return getDataFromFileViaMap();
    }

    private Map<UUID, Ticket> getDataFromFileViaMap() throws IOException{
        Map<UUID, Ticket> dataMap = new HashMap<>();
        List<Ticket> dataList = getDataFromFile();
        dataList.forEach(model -> dataMap.put(model.getId(),model));
        return dataMap;
    }

    private List<Ticket> getDataFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(file, Ticket[].class));
    }

    public void saveDataToFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file, localStorage.values());
    }

    @Override
    public Ticket create(Ticket model) {
        localStorage.put(model.getId(),model);
        return localStorage.get(model.getId());
    }

    @Override
    public List<Ticket> getAll(){
        return new ArrayList<>(localStorage.values());
    }

    @Override
    public Ticket getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id);
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(Ticket.class.getName(),id);
        }
    }

    @Override
    public Ticket update(Ticket model){
        checkIdForExisting(model.getId());
        localStorage.put(model.getId(),model);
        return getById(model.getId());
    }

    @Override
    public Ticket deleteById(UUID id){
        checkIdForExisting(id);
        return localStorage.remove(id);
    }
}
