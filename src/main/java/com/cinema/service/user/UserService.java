package com.cinema.service.user;

import com.cinema.model.User;
import com.cinema.service.CrudService;
import java.util.UUID;

public interface UserService extends CrudService<User, UUID> {
}
