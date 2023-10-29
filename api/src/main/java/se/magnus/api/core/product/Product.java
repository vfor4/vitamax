package se.magnus.api.core.product;

public class Product {
    private int productId;
    private String name;
    private int weight;
    private String serviceAddress;

    public Product() {
        productId = 0;
        name = null;
        weight = 0;
        serviceAddress = null;
    }

    public Product(int productId, String name, int weight, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void setProductId(final int productId) {
        this.productId = productId;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }
}
