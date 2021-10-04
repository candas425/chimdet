package ro.utcn.backend.model;

import ro.utcn.backend.model.factory.IEntity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Employee
 * <p>
 * Created by Lucian on 5/11/2017.
 */

@Entity
@Table(name = "angajat")
public class Employee extends IEntity {

    public static final String NUME ="nume";
    public static final String CNP ="cnp";
    public static final String DATA_ANGAJARII ="dataAngajarii";
    public static final String ZILE_CONCEDIU_RAMASE ="zileConcediuRamase";
    public static final String ORE_LUCRU_ZI ="oreLucruZi";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "NUME")
    private String nume;

    @Column(name = "CNP")
    private String cnp;

    @Column(name = "DATA_ANGAJARII")
    private LocalDate dataAngajarii;

    @Column(name = "ZILE_CONCEDIU")
    private double zileConcediuRamase;

    @Column(name = "ORE_LUCRU_ZI")
    private double oreLucruZi;

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

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public LocalDate getDataAngajarii() {
        return dataAngajarii;
    }

    public void setDataAngajarii(LocalDate dataAngajarii) {
        this.dataAngajarii = dataAngajarii;
    }

    public double getZileConcediuRamase() {
        return zileConcediuRamase;
    }

    public void setZileConcediuRamase(double zileConcediuRamase) {
        this.zileConcediuRamase = zileConcediuRamase;
    }

    public double getOreLucruZi() {
        return oreLucruZi;
    }

    public void setOreLucruZi(double oreLucruZi) {
        this.oreLucruZi = oreLucruZi;
    }
}
