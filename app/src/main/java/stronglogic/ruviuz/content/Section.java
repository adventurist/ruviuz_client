package stronglogic.ruviuz.content;


import android.app.LauncherActivity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by logicp on 12/2/2016.
 * Yeah
 */
public class Section extends LauncherActivity.ListItem implements Parcelable {

    @StringDef({EmptyType.CHIMNEY, EmptyType.SKY_LIGHT, EmptyType.OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EmptyType {
        String CHIMNEY = "Chimney";
        String SKY_LIGHT = "Skylight";
        String OTHER = "Other";
    }


    public Section() {
        this.slope = 0;
        this.width = 0;
        this.length = 0;
        this.missing = 0;
        this.id = -1;
        this.full = true;
    }

    private float slope, width, length, missing;
    private boolean full;
    private int id;

    @EmptyType
    private String emptyType;


    public void setEmptyType(@EmptyType String emptyType) {
        this.emptyType = emptyType;
    }

    @EmptyType
    public String getEmptyType() {
        return this.emptyType;
    }

    public void toggleFull() {
        this.full = !this.full;
    }

    public boolean isFull() { return this.full; }

    public void setSlope(float slope)  {
        this.slope = slope;
    }

    public float getSlope()  {
        return this.slope;
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


    public void setMissing(float area) { this.missing = area;
    }

    public float getMissing() { return this.missing; }

    public void setId(int id) { this.id = id;}
    public int getId()  { return this.id;}


    @Override
    public int describeContents() {
        return 0;
    }


    public Section(Parcel in)   {
        this.slope = in.readFloat();
        this.width = in.readFloat();
        this.length = in.readFloat();
        this.missing = in.readFloat();
        this.id = in.readInt();
        //noinspection WrongConstant
        this.emptyType = in.readString();
//        if (in.readString().equals(EmptyType.CHIMNEY)) {
//            this.emptyType = (EmptyType.CHIMNEY);
//        } else if (in.readString().equals(EmptyType.SKY_LIGHT)) {
//            this.emptyType = EmptyType.SKY_LIGHT;
//        } else if (in.readString().equals(EmptyType.OTHER)){
//            this.emptyType= EmptyType.OTHER;
//        }

    }

    public static final Parcelable.Creator<Section> CREATOR =
            new Parcelable.Creator<Section>()   {
                public Section createFromParcel(Parcel in)  {
                    return new Section(in);
                }

                public Section[] newArray(int size) {
                    return new Section[size];
                }
            };

    @Override
    public void writeToParcel(Parcel out, int flags)    {
        out.writeInt(id);
        out.writeFloat(slope);
        out.writeFloat(length);
        out.writeFloat(width);
        out.writeFloat(missing);
        out.writeString(emptyType);
    }
}