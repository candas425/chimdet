package ro.utcn.backend.model;


import ro.utcn.backend.model.enums.TipConcediu;

import javax.persistence.*;
import java.time.LocalDate;


/**
 * Zile libere
 * Created by Lucian on 6/5/2017.
 */

@Entity
@Table(name = "holiday")
public class Holiday {

    public static final String DATA = "data";
    public static final String ANGAJAT_ID = "employee.id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "TIP_CONCEDIU", nullable = false)
    private TipConcediu tipConcediu;

    @Column(name = "DATA", nullable = false)
    public LocalDate data;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ANGAJAT_ID", foreignKey = @ForeignKey(name = "ANGAJAT_ID_FK"))
    private Employee employee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public TipConcediu getTipConcediu() {
        return tipConcediu;
    }

    public void setTipConcediu(TipConcediu tipConcediu) {
        this.tipConcediu = tipConcediu;
    }
}
