package com.cinema.repository.user.impl;

import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.repository.ticket.TicketRepository;
import com.cinema.repository.user.UserRepository;
import com.cinema.utility.file.basic.FileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class UserFileRepositoryImpl implements UserRepository {
    private final FileUtility<User> fileUtility;
    private final Map<UUID, User> localStorage;
    private final TicketRepository ticketRepository;

    public UserFileRepositoryImpl(FileUtility<User> fileUtility,
                                  TicketRepository ticketRepository) {
        this.fileUtility = fileUtility;
        this.localStorage = getDataFromFileViaMap();
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void create(User model) {
        ValidatorUtility.validateModel(model);
        checkPhoneForUniqueness(model);
        save(model);
    }

    @Override
    public List<User> getAll(){
        return localStorage.values()
                .stream()
                .map(User::createCopy)
                .collect(toList());
    }

    @Override
    public User getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id).createCopy();
    }

    @Override
    public void update(User model){
        ValidatorUtility.validateModel(model);
        checkIdForExisting(model.getId());
        User oldUser = getById(model.getId());
        if(!oldUser.getPhone().equals(model.getPhone())){
            checkPhoneForUniqueness(model);
        }
        updateReferencesInTickets(model);
        save(model);
    }

    @Override
    public void deleteById(UUID id){
        checkIdForExisting(id);
        deleteRelatedTickets(getById(id));
        localStorage.remove(id);
    }

    @Override
    public void synchronize() {
        fileUtility.write(new ArrayList<>(localStorage.values()));
    }

    private void save(User model){
        localStorage.put(model.getId(), model);
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(User.class.getName(), id);
        }
    }

    private Map<UUID, User> getDataFromFileViaMap() {
        List<User> dataList = fileUtility.read();
        return convertDataListToDataMap(dataList);
    }

    private Map<UUID, User> convertDataListToDataMap(List<User> dataList){
        Map<UUID,User> dataMap = new HashMap<>();
        dataList.forEach(user -> dataMap.put(user.getId(), user));
        return dataMap;
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

    private Stream<Ticket> findReferencesInTickets(User model){
        return ticketRepository.getAll()
                .stream()
                .filter(ticket -> ticket.getUser().equals(model));
    }

    private void updateReferencesInTickets(User model){
        findReferencesInTickets(model).forEach(ticket ->
                updateReferenceInTicket(ticket,model)
        );
    }

    private void updateReferenceInTicket(Ticket ticket, User user){
        ticket.setUser(user);
        ticketRepository.update(ticket);
    }

    private void deleteRelatedTickets(User model){
        findReferencesInTickets(model).forEach(ticket ->
                ticketRepository.deleteById(ticket.getId()));
    }
}
