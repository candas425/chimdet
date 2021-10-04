package ro.utcn.frontend.frontendServices.helper;

import java.util.Map;

/**
 * Class used to maintain the good cantities and products and also the bad ones from a comand
 *
 * Created by Lucas on 7/24/2017.
 */
public class ComandaHelper {

    private Map<Integer, Integer> produsIntegerMap;
    String errorMessage;

    public Map<Integer, Integer> getProdusIntegerMap() {
        return produsIntegerMap;
    }

    public void setProdusIntegerMap(Map<Integer, Integer> produsIntegerMap) {
        this.produsIntegerMap = produsIntegerMap;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
