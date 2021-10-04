package ro.utcn.frontend.frontendServices.helper;

import java.util.Map;

/**
 *
 *
 * Created by Lucian on 4/27/2017.
 */
public class TableRowComanda {

    public static final String IDENTIFICATOR_PRODUS = "identificatorProdus";
    public static final String NUME_PRODUS = "numeProdus";
    public static final String CANTITATE_PRODUS = "cantitateProdus";
    public static final String LIST_MAP = "listMap";

    private String numeProdus;
    private Double cantitateProdus;
    private String identificatorProdus;
    private Map<String, String> listMap;

    public String getNumeProdus() {
        return numeProdus;
    }

    public void setNumeProdus(String numeProdus) {
        this.numeProdus = numeProdus;
    }

    public Double getCantitateProdus() {
        return cantitateProdus;
    }

    public void setCantitateProdus(Double cantitateProdus) {
        this.cantitateProdus = cantitateProdus;
    }

    public Map<String, String> getListMap() {
        return listMap;
    }

    public void setListMap(Map<String, String> listMap) {
        this.listMap = listMap;
    }

    public String getIdentificatorProdus() {
        return identificatorProdus;
    }

    public void setIdentificatorProdus(String identificatorProdus) {
        this.identificatorProdus = identificatorProdus;
    }
}
