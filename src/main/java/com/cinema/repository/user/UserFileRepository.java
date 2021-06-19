package com.cinema.repository.user;

import com.cinema.model.User;
import com.cinema.repository.CrudRepository;

import java.util.UUID;

public interface UserFileRepository extends CrudRepository<User, UUID> {
}
