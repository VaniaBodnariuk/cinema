package com.cinema.service.user;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.User;

import java.util.UUID;

public interface UserService extends CrudApi<User, UUID>,
                                     SaveToPersistenceApi {
}
