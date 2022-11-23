package com.misiontic.c17g4.securityBackend.services;

import com.misiontic.c17g4.securityBackend.models.User;
import com.misiontic.c17g4.securityBackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    public List<User> index(){
        return (List<User>) this.userRepository.findAll();
    }

    public Optional<User> show(int id){
        Optional<User> result = this.userRepository.findById(id);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The requested user.id does not exists.");
        return result;
    }

    public Optional<User> showByEmail(String email){
        Optional<User> result = this.userRepository.findByEmail(email);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The requested user.email does not exists.");
        return result;
    }

    public Optional<User> showByNickname(String nickname){
        Optional<User> result = this.userRepository.findByNickname(nickname);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The requested user.nickname does not exists.");
        return result;
    }

    public ResponseEntity<User> create(User newUser) {
        if (newUser.getIdUser() != null) {
            Optional<User> tempUser = this.userRepository.findById(newUser.getIdUser());
            if (tempUser.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "ID is yet in the database");
        }
        if ((newUser.getEmail() != null) && (newUser.getNickname() != null) &&
                (newUser.getPassword() != null) && (newUser.getRol() != null)) {
            newUser.setPassword(this.convertToSHA256(newUser.getPassword()));
            return new ResponseEntity<>(this.userRepository.save(newUser), HttpStatus.CREATED);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mandatory fields had not been provided.");
        }
    }

    public ResponseEntity<User> update(int id, User user){
        if (id > 0){
            Optional<User> tempUser = this.userRepository.findById(id);
            if(tempUser.isPresent()){
                if (user.getNickname() != null){
                    tempUser.get().setNickname(user.getNickname());
                }
                if (user.getPassword() != null){
                    tempUser.get().setPassword(this.convertToSHA256(user.getPassword()));
                }
                if (user.getRol() != null){
                    tempUser.get().setRol(user.getRol());
                }
                return new ResponseEntity<>(this.userRepository.save(tempUser.get()),HttpStatus.CREATED);
            }
            else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User.id does not exist in database");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User.id cannot be negative");
        }
    }

    public ResponseEntity<Boolean> delete(int id){
        Boolean success = this.show(id).map(user -> {
            this.userRepository.delete(user);
            return true;
        }).orElse(false);
        if (success){
            return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
        }
        else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "User cannot be deleted");
        }
    }

    public HashMap<String, Object> login(User user){
        HashMap<String, Object> result = new HashMap<>();
        if (user.getPassword() != null && user.getEmail() != null){
            String email = user.getEmail();
            String password = this.convertToSHA256(user.getPassword());
            Optional<User> tempUser = this.userRepository.validateLogin(email, password);
            if (tempUser.isEmpty()){
                result.put("permission", false);
            }
            else {
                result.put("permission", true);
                result.put("nickname", tempUser.get().getNickname());
            }
        }
        else {
            result.put("permission", false);
        }
        return result;
    }

    public String convertToSHA256(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
        StringBuffer sb = new StringBuffer();
        byte[] hash = md.digest(password.getBytes());
        for (byte b: hash)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
