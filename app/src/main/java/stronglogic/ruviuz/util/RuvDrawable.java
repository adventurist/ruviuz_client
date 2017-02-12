package stronglogic.ruviuz.util;

/**
 * Created by logicp on 2/11/17.
 * Helper class to work with Sections
 */

import android.graphics.drawable.GradientDrawable;

public class RuvDrawable extends GradientDrawable {

    public RuvDrawable(int pStartColor, int pCenterColor, int pEndColor, int pStrokeWidth, int pStrokeColor, float cornerRadius) {
        super(Orientation.BOTTOM_TOP,new int[]{pStartColor,pCenterColor,pEndColor});
        setStroke(pStrokeWidth,pStrokeColor);
        setShape(GradientDrawable.RECTANGLE);
        setCornerRadius(cornerRadius);
    }

}