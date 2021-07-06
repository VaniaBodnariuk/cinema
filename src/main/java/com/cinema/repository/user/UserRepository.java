package com.cinema.repository.user;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.User;

import java.util.UUID;

public interface UserRepository extends CrudApi<User, UUID>,
                                        SaveToPersistenceApi {
}
