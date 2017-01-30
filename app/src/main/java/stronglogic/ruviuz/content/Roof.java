package stronglogic.ruviuz.content;


import android.app.LauncherActivity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by logicp on 12/2/2016.
 * Yeah
 */
public class Roof extends LauncherActivity.ListItem implements Serializable  {

    private String address;
    private float slope;
    private BigDecimal price;
    private float width;
    private float length;
    private int id;
    private String customerName;
    private boolean justUpdated;

    private ArrayList<String> photos;


    public void setAddress(String address)    {
        this.address = address;
    }

    public String getAddress()  {
        return this.address;
    }


    public void setSlope(float slope)  {
        this.slope = slope;
    }

    public float getSlope()  {
        return this.slope;
    }


    public void setPrice(BigDecimal price)  {
        this.price = price;
    }

    public BigDecimal getPrice()  {
        return price;
    }


    public void setWidth (float width)  {
        this.width = width;
    }

    public float getWidth()  {
        return width;
    }


    public void setLength(float length)  {
        this.length = length;
    }

    public float getLength()  {
        return this.length;
    }


    public void setId(int id)  {
        this.id = id;
    }

    public int getId()  { return this.id;}


    public void setPhotos (ArrayList<String> urls)  {
        this.photos = urls;
    }

    public ArrayList<String> getPhotos() {
        if (this.photos != null) {
            return this.photos;
        } else {
            return new ArrayList<String>();
        }
    }

    public void setCustomerName(String name) {
        this.customerName = name;
    }

    public String getCustomerName() {
        return this.customerName;
    }


    public void toggleJustUpdated() {
        this.justUpdated = !this.justUpdated;
    }

    public boolean isUpdated() {
        return this.justUpdated;
    }
}