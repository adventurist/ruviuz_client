package stronglogic.ruviuz.views;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by logicp on 3/30/17.
 * Defines Ruv-specific methods
 */

public abstract class RuvBaseAdapter extends RecyclerView.Adapter {

    private ArrayList<?> list;

    public void swapData(ArrayList<?> list) {
        this.list.clear();
        this.list = list;
    }
}
