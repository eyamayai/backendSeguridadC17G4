package com.misiontic.c17g4.securityBackend.services;

import com.misiontic.c17g4.securityBackend.models.Permission;
import com.misiontic.c17g4.securityBackend.models.Rol;
import com.misiontic.c17g4.securityBackend.repositories.PermissionRepository;
import com.misiontic.c17g4.securityBackend.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RolServices {
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Rol> index(){
        return (List<Rol>) this.rolRepository.findAll();
    }

    public Optional<Rol> show(int id){
        Optional<Rol> result = this.rolRepository.findById(id);
        if(result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Requested rol does not exist in the database.");
        return result;
    }

    public ResponseEntity<Rol> create(Rol newRol){
        if(newRol.getIdRol() != null) {
            Optional<Rol> result = this.rolRepository.findById(newRol.getIdRol());
            if (result.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "An existing id is provided. Please, remove from request.");
        }
        if(newRol.getName() != null) {
            Optional<Rol> tempRol = this.rolRepository.findByName(newRol.getName());
            if (tempRol.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Name is already in the database");
            return new ResponseEntity<>(this.rolRepository.save(newRol), HttpStatus.ACCEPTED);
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mandatory fields have not been provided.");
    }

    public ResponseEntity<Rol> update(int id, Rol updatedRol){
        if(id > 0){
            Optional<Rol> tempRol = this.show(id);
            if(tempRol.isPresent()){
                if(updatedRol.getName() != null)
                    tempRol.get().setName(updatedRol.getName());
                if(updatedRol.getDescription() != null)
                    tempRol.get().setDescription(updatedRol.getDescription());
                try {
                    return new ResponseEntity<>(this.rolRepository.save(tempRol.get()), HttpStatus.CREATED);
                }
                catch(Exception ex){
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Database is getting a conflict by a constraint.");
                }
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Requested rol does not exist in the database.");
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rol ID cannot be negative.");
    }

    public ResponseEntity<Rol> addPermission(int idRol, int idPermission) {
        Optional<Rol> rol = this.rolRepository.findById(idRol);
        if(rol.isPresent()) {
            Optional<Permission> permission = this.permissionRepository.findById(idPermission);
            if (permission.isPresent()) {
                Set<Permission> tempPermissions = rol.get().getPermissions();
                if(tempPermissions.contains(permission))
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Rol already has the permission.");
                tempPermissions.add(permission.get());
                rol.get().setPermissions(tempPermissions);
                return new ResponseEntity<>(this.rolRepository.save(rol.get()), HttpStatus.CREATED);
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Provided permission does not exist in the database.");
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Provided rol does not exist in the database.");
    }

    public ResponseEntity<Rol> removePermission(int idRol, int idPermission) {
        Optional<Rol> rol = this.rolRepository.findById(idRol);
        if(rol.isPresent()) {
            Optional<Permission> permission = this.permissionRepository.findById(idPermission);
            if (permission.isPresent()) {
                Set<Permission> tempPermissions = rol.get().getPermissions();
                if(!tempPermissions.contains(permission))
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Rol does not have the permission.");
                tempPermissions.remove(permission.get());
                rol.get().setPermissions(tempPermissions);
                return new ResponseEntity<>(this.rolRepository.save(rol.get()), HttpStatus.CREATED);
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Provided permission does not exist in the database.");
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Provided rol does not exist in the database.");
    }

    public ResponseEntity<Boolean> delete(int id){
        Boolean success = this.show(id).map(rol -> {
            this.rolRepository.delete(rol);
            return true;
        }).orElse(false);
        if(success)
            return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
        else
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Rol cannot be deleted. Check if it exists in database.");
    }

    public ResponseEntity<Boolean> validateGrant(int idRol, Permission permission){
        boolean isGrant = false;
        Optional<Rol> tempRol = this.rolRepository.findById(idRol);
        if(tempRol.isPresent()){
            for(Permission rolPermission: tempRol.get().getPermissions())
                if(rolPermission.getUrl().equals(permission.getUrl()) &&
                        rolPermission.getMethod().equals(permission.getMethod())){
                    isGrant = true;
                    break;
                }
            if(isGrant)
                return new ResponseEntity<>(true, HttpStatus.OK);
            else
                return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided rol does not exist in the database.");
    }
}
