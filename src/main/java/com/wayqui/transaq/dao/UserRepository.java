package com.wayqui.transaq.dao;

import com.wayqui.transaq.entity.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<AppUser, Long> {

    AppUser findByUsername(String username);

}
