package com.faustas.dbms.utils;

public class UserRegistrationInfo {

    private final String name;

    private final String email;

    private final String password;

    public UserRegistrationInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {

        private String name;

        private String email;

        private String password;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserRegistrationInfo build() {
            return new UserRegistrationInfo(name, email, password);
        }
    }
}
