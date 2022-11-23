package com.misiontic.c17g4.securityBackend.services;

import com.misiontic.c17g4.securityBackend.models.Permission;
import com.misiontic.c17g4.securityBackend.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServices {
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> index(){
        return (List<Permission>) this.permissionRepository.findAll();
    }

    public Optional<Permission> show(int id){
        Optional<Permission> result = this.permissionRepository.findById(id);
        if(result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Requested permission does not exist in the database.");
        return result;
    }

    public ResponseEntity<Permission> create(Permission newPermission){
        if(newPermission.getIdPermission() != null) {
            Optional<Permission> result = this.permissionRepository.findById(newPermission.getIdPermission());
            if (result.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "An existing id is provided. Please, remove from request.");
        }
        if(newPermission.getUrl() != null && newPermission.getMethod() != null) {
            Optional<Permission> tempPermission = this.permissionRepository.findByUrl(newPermission.getUrl());
            if(tempPermission.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "URL is already in the database");
            return new ResponseEntity<>(this.permissionRepository.save(newPermission), HttpStatus.CREATED);
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mandatory fields have not been provided.");
    }

    public ResponseEntity<Permission> update(int id, Permission updatedPermission){
        if(id > 0){
            Optional<Permission> tempPermission = this.show(id);
            if(tempPermission.isPresent()){
                if(updatedPermission.getMethod() != null)
                    tempPermission.get().setMethod(updatedPermission.getMethod());
                if(updatedPermission.getUrl() != null)
                    tempPermission.get().setUrl(updatedPermission.getUrl());
                try {
                    return new ResponseEntity<>(this.permissionRepository.save(tempPermission.get()), HttpStatus.CREATED);
                }
                catch(Exception ex){
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Database is getting a conflict by a constraint.");
                }
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Requested permission does not exist in the database.");
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Permission ID cannot be negative.");
    }

    public ResponseEntity<Boolean> delete(int id){
        Boolean success = this.show(id).map(permission -> {
            this.permissionRepository.delete(permission);
            return true;
        }).orElse(false);
        if(success)
            return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
        else
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Permission cannot be deleted. Check if it exists in database.");
    }
}
