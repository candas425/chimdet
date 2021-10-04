package ro.utcn.backend.model;

import org.hibernate.internal.util.StringHelper;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucian on 4/7/2017.
 */

@Entity
@Table(name = "comanda")
public class Command {

    public static final String ORAS = "oras";
    public static final String DATA = "data";
    public static final String FIRMA_ID = "business.firmaId";
    public static final String CLIENT = "client";
    public static final String FIRMA = "business";
    public static final String FIRMA_NUME = "business.nume";
    public static final String CONFIRMARE = "confirmare";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lista_produse_comanda")
    private Map<Integer, Integer> listaProduse = new HashMap<>();

    @Column(name = "DATA", nullable = false)
    public LocalDateTime data;

    @OneToOne
    @JoinColumn(name = "FIRMA_ID", foreignKey = @ForeignKey(name = "FIRMA_ID_FK"))
    private Business business;

    @Column(name = "ORAS")
    private String oras;

    @Column(name = "NUME_FISIER")
    private String numeFisier;

    @Column(name = "CONFIRMARE")
    private String confirmare;

    @Column(name = "AVAILABLE_TABLE_COMANDA")
    private boolean availableInTableComanda;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getListaProduse() {
        return listaProduse;
    }

    public void setListaProduse(Map<Integer, Integer> listaProduse) {
        this.listaProduse = listaProduse;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getOras() {
        return oras;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    public String getNumeFisier() {
        return numeFisier;
    }

    public void setNumeFisier(String numeFisier) {
        this.numeFisier = numeFisier;
    }

    public String getConfirmare() {
        return confirmare;
    }

    public void setConfirmare(String confirmare) {
        this.confirmare = confirmare;
    }

    public boolean isAvailableInTableComanda() {
        return availableInTableComanda;
    }

    public void setAvailableInTableComanda(boolean availableInTableComanda) {
        this.availableInTableComanda = availableInTableComanda;
    }

    @Override
    public String toString() {
        String toString = "";
        toString += "Data: " +data;
        toString += !StringHelper.isEmpty(oras) ? "Oras: " + oras : "";
        toString += business != null ? business : "";

        return toString;
    }
}
