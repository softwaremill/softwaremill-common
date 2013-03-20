package com.softwaremill.common.dbtest;

import javax.validation.constraints.NotNull;

/**
 * @author Maciej Bilas
 * @since 15/12/11 12:46
 */
public class Entity {

    @NotNull
    private String field;

    Entity() {
    }

    Entity(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
