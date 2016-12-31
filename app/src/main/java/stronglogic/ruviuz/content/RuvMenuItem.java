package stronglogic.ruviuz.content;

import android.app.LauncherActivity;

import java.io.Serializable;

/**
 * Created by logicp on 12/29/16.
 * Customer data holder for Ruvius' side menu
 */

public class RuvMenuItem extends LauncherActivity.ListItem implements Serializable {

    public int icon;
    public String name;
    private type mType;
    public String actionName;

    public enum type { ACTIVITY, FRAGMENT, INTENT_ACTION };

    public RuvMenuItem(int icon, String name, type mType, String actionName) {
        this.icon = icon;
        this.name = name;
        this.mType = mType;
        this.actionName = actionName;
    }

    public type getType() {
        return this.mType;
    }
}
