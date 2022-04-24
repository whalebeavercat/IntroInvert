package com.example.introinvert.models;

import java.io.Serializable;
import java.util.Objects;

public class Users implements Serializable {
    public String name, image, email, token, id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(name, users.name) && Objects.equals(email, users.email) && Objects.equals(id, users.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image, email, token, id);
    }
}
