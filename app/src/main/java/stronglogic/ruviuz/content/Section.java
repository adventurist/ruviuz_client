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

    @StringDef({SectionType.HIP_SQUARE, SectionType.HIP_RECTANGLE, SectionType.GABLE, SectionType.MANSARD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SectionType {
        String HIP_SQUARE = "Hip:Square";
        String HIP_RECTANGLE = "Hip:Rectangular";
        String GABLE = "Gable";
        String MANSARD = "Mansard";
    }


    public Section() {
        this.slope = 0;
        this.width = 0;
        this.length = 0;
        this.missing = 0;
        this.id = -1;
        this.full = true;
    }

    private float slope, width, tWidth, length, missing;
    private boolean full;
    private int id;

    @EmptyType
    private String emptyType;

    @SectionType
    private String sectionType;


    public void setEmptyType(@EmptyType String emptyType) {
        this.emptyType = emptyType;
    }

    @EmptyType
    public String getEmptyType() {
        return this.emptyType;
    }


    public void setSectionType(@SectionType String sectionType) { this.sectionType = sectionType; }

    @SectionType
    public String getSectionType() { return this.sectionType; }


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


    public void setTopWidth (float tWidth)  {
        this.tWidth = tWidth;
    }

    public float getTopWidth()  {
        return tWidth;
    }


    public void setLength(float length)  {
        this.length = length;
    }

    public float getLength()  {
        return this.length;
    }


    public void setMissing(float area) { this.missing = area; }

    public float getMissing() { return this.missing; }

    public void setFull(boolean full) { this.full = full;}


    public void setId(int id) { this.id = id;}

    public int getId()  { return this.id;}


    @Override
    public int describeContents() {
        return 0;
    }


    public Section(Parcel in)   {
        this.id = in.readInt();
        this.slope = in.readFloat();
        this.length = in.readFloat();
        this.width = in.readFloat();
        this.tWidth = in.readFloat();
        this.missing = in.readFloat();
        //noinspection WrongConstant
        this.emptyType = in.readString();
        //noinspection WrongConstant
        this.sectionType = in.readString();
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
        out.writeFloat(tWidth);
        out.writeFloat(missing);
        out.writeString(emptyType);
        out.writeString(sectionType);
    }
}