package ro.utcn.backend.model;

import javax.persistence.*;

/**
 *
 * Created by Lucian on 3/31/2017.
 */

@Entity
@Table(name = "setaregenerala")
public class GeneralSetting {

    public static final String NUME_PROPRIETATE = "numeProprietate";
    public static final String VALOARE_PROPRIETATE = "valoareProprietate";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "NUME", nullable = false)
    private String numeProprietate;


    @Column(name = "VALOARE", nullable = false)
    private String valoareProprietate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeProprietate() {
        return numeProprietate;
    }

    public void setNumeProprietate(String numeProprietate) {
        this.numeProprietate = numeProprietate;
    }

    public String getValoareProprietate() {
        return valoareProprietate;
    }

    public void setValoareProprietate(String valoareProprietate) {
        this.valoareProprietate = valoareProprietate;
    }
}
