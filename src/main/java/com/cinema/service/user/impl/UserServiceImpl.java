package com.cinema.service.user.impl;

import com.cinema.model.User;
import com.cinema.repository.user.UserRepository;
import com.cinema.service.user.UserService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void create(User model) {
        userRepository.create(model);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(UUID uuid) {
        return userRepository.getById(uuid);
    }

    @Override
    public void update(User model) {
        userRepository.update(model);
    }

    @Override
    public void deleteById(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public void synchronize() throws IOException {
        userRepository.synchronize();
    }
}
