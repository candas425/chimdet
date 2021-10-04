package ro.utcn.backend.model;

import ro.utcn.backend.model.factory.IEntity;

import javax.persistence.*;

/**
 * Price
 * Created by Lucian on 5/31/2017.
 */

@Entity
@Table(name = "pret")
public class Price extends IEntity implements Comparable<Price> {

    public static final String VALOARE = "pretUnitar";
    public static final String NUME_PRODUS = "product.nume";
    public static final String PRODUS_ID = "product.id";
    public static final String CANTITATE_PRODUS = "product.cantitate";
    public static final String FIRMA = "business.id";
    public static final String PRODUS = "product";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @OneToOne
    @JoinColumn(name = "FIRMA_ID", foreignKey = @ForeignKey(name = "BUSINESS_ID_FK"))
    private Business business;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUS_ID", foreignKey = @ForeignKey(name = "PRODUS_ID_FK"))
    private Product product;

    @Column(name = "PRET", nullable = false)
    private double pretUnitar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public double getPretUnitar() {
        return pretUnitar;
    }

    public void setPretUnitar(double pretUnitar) {
        this.pretUnitar = pretUnitar;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int compareTo(Price o) {
        return (int) (this.getProduct().getCantitate() - o.getProduct().getCantitate());
    }
}
