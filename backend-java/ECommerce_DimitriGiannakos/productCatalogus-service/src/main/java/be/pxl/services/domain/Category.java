package be.pxl.services.domain;

public enum Category {
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    FOOD("Food");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
