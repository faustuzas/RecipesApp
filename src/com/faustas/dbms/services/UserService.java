package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Encryptor;
import com.faustas.dbms.models.User;
import com.faustas.dbms.repositories.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    private Encryptor encryptor;

    public UserService(UserRepository userRepository, Encryptor encryptor) {
        this.userRepository = userRepository;
        this.encryptor = encryptor;
    }

    public User findByEmailAndPassword(String email, String password) {
        String encryptedPassword = encryptor.encrypt(password);
        return userRepository.findByEmailAndPassword(email, encryptedPassword);
    }

    public void save(User user) {
        String encryptedPassword = encryptor.encrypt(user.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.insert(user);
    }
}

