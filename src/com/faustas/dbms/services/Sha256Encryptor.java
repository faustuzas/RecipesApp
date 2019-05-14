package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Encryptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class Sha256Encryptor implements Encryptor {

    private MessageDigest digestor;

    public Sha256Encryptor() {
        try {
            this.digestor = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encrypt(String string) {
        return new String(digestor.digest(string.getBytes(StandardCharsets.UTF_8)));
    }
}
