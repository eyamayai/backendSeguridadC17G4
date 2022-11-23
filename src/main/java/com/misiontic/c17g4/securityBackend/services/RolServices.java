package com.misiontic.c17g4.securityBackend.services;

import com.misiontic.c17g4.securityBackend.models.Rol;
import com.misiontic.c17g4.securityBackend.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServices {
    @Autowired
    private RolRepository rolRepository;

    public List<Rol> index(){
        return (List<Rol>) this.rolRepository.findAll();
    }

    public Optional<Rol> show(int id){
        return this.rolRepository.findById(id);
    }

    public Rol create(Rol newRol){
        if (newRol.getIdRol() == null){
            if (newRol.getName() != null){
                return this.rolRepository.save(newRol);
            }
            else {
                return newRol;
            }
        }
        else {
            return newRol;
        }
    }

    public Rol update(int id, Rol updatedRol){
        if (id > 0){
            Optional<Rol> tempRol = this.show(id);
            if (tempRol.isPresent()){
                if(updatedRol.getName() != null){
                    tempRol.get().setName(updatedRol.getName());
                }
                if(updatedRol.getDescription() != null){
                    tempRol.get().setDescription((updatedRol.getDescription()));
                }
                return this.rolRepository.save(tempRol.get());
            }
            else {
                return updatedRol;
            }
        }
        else {
            return updatedRol;
        }
    }

    public boolean delete(int id){
        Boolean success = this.show(id).map(rol -> {
            this.rolRepository.delete(rol);
            return true;
        }).orElse(false);
        return success;
    }
}
