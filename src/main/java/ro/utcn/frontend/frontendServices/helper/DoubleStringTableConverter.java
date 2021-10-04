package ro.utcn.frontend.frontendServices.helper;

import javafx.util.converter.DoubleStringConverter;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * 
 * Created by Lucas on 4/16/2017.
 */
public class DoubleStringTableConverter extends DoubleStringConverter {

    @Override public Double fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        double valueFinal = 0;
        try{
            valueFinal = Double.valueOf(value);
            return valueFinal;
        }catch (Exception e){
            alertWithError("Introdu doar numere separate de un punct");
        }
        return valueFinal;
    }
    
}
