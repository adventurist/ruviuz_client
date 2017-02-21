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
    private BigDecimal price;
    private int floors;
    private int cleanUpFactor;
    private int id;
    private String customerName;
    private boolean justUpdated;

    private ArrayList<Section> sections;

    private ArrayList<String> photos;

    private ArrayList<RuvFileInfo> ruvFiles;


    public void setAddress(String address)    {
        this.address = address;
    }

    public String getAddress()  {
        return this.address;
    }


    public void setFloors(int floors)  {
        this.floors = floors;
    }

    public int getFloors()  {
        return this.floors;
    }


    public void setPrice(BigDecimal price)  {
        this.price = price;
    }

    public BigDecimal getPrice()  {
        return price;
    }


    public void setCleanUpFactor (int cleanUpFactor)  {
        this.cleanUpFactor = cleanUpFactor;
    }

    public int getCleanUpFactor()  {
        return cleanUpFactor;
    }


//    public void setLength(float length)  {
//        this.length = length;
//    }
//
//    public float getLength()  {
//        return this.length;
//    }


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


    public void setFiles (ArrayList<RuvFileInfo> files)  {
        this.ruvFiles = files;
    }

    public ArrayList<RuvFileInfo> getFiles() {
        if (this.ruvFiles != null) {
            return this.ruvFiles;
        } else {
            return new ArrayList<RuvFileInfo>();
        }
    }

    public void setSections (ArrayList<Section> sectionList) { this.sections = sectionList; }

    public ArrayList<Section> getSections() {
        if (this.sections != null) {
            return this.sections;
        } else {
            return new ArrayList<Section>();
        }
    }

    public void setCustomerName(String name) {
        this.customerName = name;
    }

    public String getCustomerName() {
        return this.customerName == null ? "John Doe" : this.customerName;
    }


    public void toggleJustUpdated() {
        this.justUpdated = !this.justUpdated;
    }

    public boolean isUpdated() {
        return this.justUpdated;
    }
}