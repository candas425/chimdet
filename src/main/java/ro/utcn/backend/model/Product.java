package ro.utcn.backend.model;

import ro.utcn.backend.model.enums.TipProdus;
import ro.utcn.backend.model.factory.IEntity;

import javax.persistence.*;
import java.lang.reflect.Field;

/**
 * Folosit pentru a descrie un produs
 * <p>
 * Created by Lucian on 3/25/2017.
 */

@Entity
@Table(name = "produs")
public class Product extends IEntity {

    public static final String IDENTIFICATOR_PRODUS = "identificatorProdus";
    public static final String NUME = "nume";
    public static final String CANTITATE = "cantitate";
    public static final String TIP_PRODUS = "tip";
    public static final String EXISTA_IN_TABEL_COMENZI = "existaInTabelComenzi";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ID_PRODUS")
    private String identificatorProdus;

    @Column(name = "NUME", nullable = false)
    private String nume;

    @Column(name = "CANTITATE")
    private double cantitate;

    @Column(name = "TIP_PRODUS", nullable = false)
    private TipProdus tip;

    @Column(name = "EXISTA_IN_TABEL_COMENZI")
    private String existaInTabelComenzi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public double getCantitate() {
        return cantitate;
    }

    public void setCantitate(double cantitate) {
        this.cantitate = cantitate;
    }

    public TipProdus getTip() {
        return tip;
    }

    public void setTip(TipProdus tip) {
        this.tip = tip;
    }

    public String getIdentificatorProdus() {
        return identificatorProdus;
    }

    public void setIdentificatorProdus(String identificatorProdus) {
        this.identificatorProdus = identificatorProdus;
    }

    public String getExistaInTabelComenzi() {
        return existaInTabelComenzi;
    }

    public void setExistaInTabelComenzi(String existaInTabelComenzi) {
        this.existaInTabelComenzi = existaInTabelComenzi;
    }

    public void setField(String fieldName, TipProdus value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, value);
    }

    @Override
    public String toString() {
        return (identificatorProdus != null ? identificatorProdus + " - " : "") + cantitate + "L - " + nume;
    }

}
