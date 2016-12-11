package stronglogic.ruviuz.content;


import android.app.LauncherActivity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
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
    private boolean justUpdated;

    private ArrayList<URL> photos;

    private enum status{};

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


    public void setPhotos (ArrayList<URL> urls)  {
        this.photos = urls;
    }

    public ArrayList<URL> getPhotos() { return new ArrayList<URL>(this.photos);}


    public void toggleJustUpdated() {
        this.justUpdated = !this.justUpdated;
    }

    public boolean isUpdated() {
        return this.justUpdated;
    }
}