package ro.utcn.frontend.frontendServices.helper;

import ro.utcn.backend.model.Product;

/**
 *
 * Created by Lucian on 5/5/2017.
 */
public class ListRowComanda {

    private Product product;
    private double cantitate;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getCantitate() {
        return cantitate;
    }

    public void setCantitate(double cantitate) {
        this.cantitate = cantitate;
    }

    @Override
    public String toString() {
        return "Cantitate: " + cantitate + " Product: " + product;
    }
}
