package com.cinema.repository.user.impl;

import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.repository.ticket.TicketFileRepository;
import com.cinema.repository.user.UserFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

//TODO Include FileUtility and format code
public class UserFileRepositoryImpl implements UserFileRepository {
    private final File file;
    private final Map<UUID, User> localStorage;
    private final TicketFileRepository ticketFileRepository;

    public UserFileRepositoryImpl(File file,
                                  TicketFileRepository ticketFileRepository)
            throws IOException {
        this.file = file;
        this.localStorage = initLocalStorage();
        this.ticketFileRepository = ticketFileRepository;
    }

    private Map<UUID, User> initLocalStorage() throws IOException {
        if(file.length() == 0){
            return new HashMap<>();
        }
        return getDataFromFileViaMap();
    }

    private Map<UUID, User> getDataFromFileViaMap() throws IOException{
        Map<UUID, User> dataMap = new HashMap<>();
        List<User> dataList = getDataFromFile();
        dataList.forEach(model -> dataMap.put(model.getId(),model));
        return dataMap;
    }

    private List<User> getDataFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(file, User[].class));
    }

    public void saveDataToFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file, localStorage.values());
    }

    @Override
    public User create(User model) {
        checkPhoneForUniqueness(model);
        localStorage.put(model.getId(),model);
        return localStorage.get(model.getId());
    }

    private void checkPhoneForUniqueness(User model) {
        if(isPhoneAlreadyExists(model)){
            throw new UniqueFieldException(model.getClass().getName(),
                    model.getId(),"phone");
        }
    }

    private boolean isPhoneAlreadyExists(User model){
        return getAll().stream()
                .anyMatch(user -> user.getPhone()
                                      .equals(model.getPhone()));
    }

    @Override
    public List<User> getAll(){
        return new ArrayList<>(localStorage.values());
    }

    @Override
    public User getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id);
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(User.class.getName(),id);
        }
    }

    @Override
    public User update(User model){
        checkIdForExisting(model.getId());
        checkPhoneForUniqueness(model);
        updateReferencesInTickets(model);
        localStorage.put(model.getId(),model);
        return getById(model.getId());
    }

    private Stream<Ticket> findReferencesInTickets(User model){
        return ticketFileRepository.getAll()
                .stream()
                .filter(movie -> movie.getUser().equals(model));
    }

    private void updateReferencesInTickets(User model){
        findReferencesInTickets(model).forEach(movie -> movie.setUser(model));
    }

    @Override
    public User deleteById(UUID id){
        checkIdForExisting(id);
        deleteRelatedTickets(getById(id));
        return localStorage.remove(id);
    }

    private void deleteRelatedTickets(User model){
        findReferencesInTickets(model).forEach(movie
                -> ticketFileRepository.deleteById(movie.getId()));
    }
}
