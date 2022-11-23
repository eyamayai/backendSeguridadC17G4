package com.misiontic.c17g4.securityBackend.controllers;

import com.misiontic.c17g4.securityBackend.models.Rol;
import com.misiontic.c17g4.securityBackend.services.RolServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/rol")
public class RolController {
    @Autowired
    private RolServices rolServices;

    @GetMapping("/all")
    public List<Rol> getAllRoles(){
        return this.rolServices.index();
    }

    @GetMapping("/{id}")
    public Optional<Rol> getRolById(@PathVariable("id") int id){
        return this.rolServices.show(id);
    }

    @PostMapping("/insert")
    @ResponseStatus(HttpStatus.CREATED)
    public Rol insertRol(@RequestBody Rol rol){
        return this.rolServices.create(rol);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Rol updateRol(@PathVariable("id") int id, @RequestBody Rol rol){
        return this.rolServices.update(id, rol);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean deleteRol(@PathVariable("id") int id){
        return this.rolServices.delete(id);
    }
}
