package fire.half_blood_prince.myapplication.model;

import java.util.Locale;

/**
 * Created by Half-Blood-Prince on 1/11/2017.
 * Category Model class
 */

public class Category {

    private int id;
    private String catName;
    private String catType;

    private String totalAmount;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "id = %d catName = %s catType = %s totalAmount = %s"
                , id, catName, catType, totalAmount);
    }


    // getters and setters


    public int getId() {
        return id;
    }

    public String getCatName() {
        return catName;
    }

    public String getCatType() {
        return catType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
