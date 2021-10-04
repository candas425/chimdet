package ro.utcn.backend.model.factory;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 *
 * Created by Lucas on 4/15/2017.
 */
public abstract class IEntity {

    public void setField(String coloana, String value) throws NoSuchFieldException, IllegalAccessException{
        Field field = getClass().getDeclaredField(coloana);
        field.setAccessible(true);
        field.set(this, value);
    }

    public void setField(String coloana, Double value) throws NoSuchFieldException, IllegalAccessException{
        Field field = getClass().getDeclaredField(coloana);
        field.setAccessible(true);
        field.set(this, value);
    }

    public void setField(String fieldName, LocalDate localDate) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, localDate);
    }

    public Object getFieldValue(String fieldName, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(type);
    }


}
