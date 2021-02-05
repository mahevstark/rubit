package net.trejj.talk.model;

public class CreditsModel {
    private String id;
    private String credits;
    private String sku;
    private String type;
    private String title;
    private String price;
    private String price_usd;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public String getId() {
        return id;
    }

    public String getCredits() {
        return credits;
    }

    public String getSku() {
        return sku;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public CreditsModel(String id, String credits, String sku, String type, String title, String price,
                        String price_usd, String desc) {
        this.id = id;
        this.credits = credits;
        this.sku = sku;
        this.type = type;
        this.title = title;
        this.price = price;
        this.price_usd = price_usd;
        this.desc = desc;
    }

    public String getPrice_usd() {
        return price_usd;
    }

    public void setPrice_usd(String price_usd) {
        this.price_usd = price_usd;
    }

    public String getPrice() {
        return price;
    }
}
