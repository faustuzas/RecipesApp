package com.faustas.dbms.models;

import com.faustas.dbms.interfaces.Identifiable;

public class Ingredient implements Identifiable {

    private Integer id;

    private String amount;

    private Product product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", amount='" + amount + '\'' +
                ", product=" + product +
                '}';
    }
}
