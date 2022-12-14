package com.misiontic.c17g4.securityBackend.repositories;

import com.misiontic.c17g4.securityBackend.models.Rol;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends CrudRepository<Rol, Integer> {
    Optional<Rol> findByName(String name);
}
