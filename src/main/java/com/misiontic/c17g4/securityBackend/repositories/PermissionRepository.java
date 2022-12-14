package com.misiontic.c17g4.securityBackend.repositories;

import com.misiontic.c17g4.securityBackend.models.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Integer> {
    Optional<Permission> findByUrl(String url);
}
