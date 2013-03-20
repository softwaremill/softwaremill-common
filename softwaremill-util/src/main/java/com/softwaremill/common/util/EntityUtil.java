package com.softwaremill.common.util;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Entity-based util methods
 */
public class EntityUtil {

    /**
     * Evaluates given string path (splitting by dot) and returns the desired path
     *
     * @param root Root to start with
     * @param path Result path
     * @return Path to desired property
     */
    public static Path getCriteriaPath(Root root, String path) {
        String[] splittedPath = path.split("\\.");

        Path result = root.get(splittedPath[0]);

        for (int i = 1; i < splittedPath.length; i++) {
            result = result.get(splittedPath[i]);
        }

        return result;
    }

    /**
     * Checks if the given field is a mapped JPA field
     *
     * @param field Field
     * @return True if mapped, false otherwise
     */
    public static boolean isMappedField(Field field) {
        return field.getAnnotation(Column.class) != null || field.getAnnotation(OneToMany.class) != null
                || field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null
                || field.getAnnotation(ManyToMany.class) != null;
    }

    /**
     * Returns if this field is an owning side of a JPA relation
     *
     * @param field Field
     * @return true or false
     */
    public static boolean isOwningSide(Field field) {
        if (field.getAnnotation(OneToMany.class) != null) {
            return field.getAnnotation(OneToMany.class).mappedBy().equals("");
        }

        if (field.getAnnotation(OneToOne.class) != null) {
            return field.getAnnotation(OneToOne.class).mappedBy().equals("");
        }

        if (field.getAnnotation(ManyToMany.class) != null) {
            return field.getAnnotation(ManyToMany.class).mappedBy().equals("");
        }

        return true;
    }

    /**
     * Returns the entity class representing the member of collection
     */
    public static Class getMemberClass(Field field) {
        return (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }
}