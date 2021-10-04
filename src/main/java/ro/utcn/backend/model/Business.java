package ro.utcn.backend.model;

import ro.utcn.backend.model.enums.TipFirma;
import ro.utcn.backend.model.factory.IEntity;

import javax.persistence.*;
import java.lang.reflect.Field;

/**
 * Folosit pentru a descrie firma, cumparatorul sau vanzatorul de pe factura
 *
 * Created by Lucian on 3/25/2017.
 */

@Entity
@Table(name = "firma")
public class Business extends IEntity {

    public static final String EXISTA_IN_COMENZI  = "availableInCommands";
    public static final String NUME = "nume";
    public static final String SEDIUL = "sediul";
    public static final String CUI = "cui";
    public static final String REGCOM = "regCom";
    public static final String JUDET = "judet";
    public static final String MAIL = "mail";
    public static final String TIP_FIRMA = "tip";
    public static final String COMANDA_TABEL ="comandaTabel";
    public static final String LAST_RESET_TIME ="lastResetTime";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "NUME", nullable = false)
    private String nume;

    @Column(name = "SEDIUL")
    private String sediul;

    @Column(name = "CUI", nullable = false)
    private String cui;

    @Column(name = "REG_COM")
    private String regCom;

    @Column(name = "JUDET")
    private String judet;

    @Column(name = "MAIL")
    private String mail;

    @Column(name = "TIP_FIRMA")
    private TipFirma tip;

    @Column(name = "EXISTA_IN_COMENZI")
    private String availableInCommands;

    @Column(name = "COMANDA_TABEL")
    private String comandaTabel;

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

    public String getSediul() {
        return sediul;
    }

    public void setSediul(String sediul) {
        this.sediul = sediul;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getRegCom() {
        return regCom;
    }

    public void setRegCom(String regCom) {
        this.regCom = regCom;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public TipFirma getTip() {
        return tip;
    }

    public void setTip(TipFirma tip) {
        this.tip = tip;
    }

    public String getAvailableInCommands() {
        return availableInCommands;
    }

    public void setAvailableInCommands(String availableInCommands) {
        this.availableInCommands = availableInCommands;
    }

    public String getComandaTabel() {
        return comandaTabel;
    }

    public void setComandaTabel(String comandaTabel) {
        this.comandaTabel = comandaTabel;
    }

    @Override
    public String toString(){
        return this.nume;
    }

    public void setField(String fieldName, TipFirma value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, value);
    }
}
