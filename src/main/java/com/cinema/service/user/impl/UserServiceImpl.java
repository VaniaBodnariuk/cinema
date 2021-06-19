package com.cinema.service.user.impl;

import com.cinema.model.User;
import com.cinema.repository.user.UserFileRepository;
import com.cinema.service.user.UserService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserFileRepository userFileRepository;

    @Override
    public User create(User model) {
        return userFileRepository.create(model);
    }

    @Override
    public List<User> getAll() {
        return userFileRepository.getAll();
    }

    @Override
    public User getById(UUID uuid) {
        return userFileRepository.getById(uuid);
    }

    @Override
    public User update(User model) {
        return userFileRepository.update(model);
    }

    @Override
    public User deleteById(UUID uuid) {
        return userFileRepository.deleteById(uuid);
    }
}
