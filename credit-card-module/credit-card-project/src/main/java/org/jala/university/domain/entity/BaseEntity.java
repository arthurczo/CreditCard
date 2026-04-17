package org.jala.university.domain.entity;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public abstract class BaseEntity<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private ID id;

    public ID getId() {
        return id;
    }

}
