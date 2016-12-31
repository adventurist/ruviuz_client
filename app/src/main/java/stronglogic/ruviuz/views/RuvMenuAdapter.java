package stronglogic.ruviuz.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.RuvMenuItem;

/**
 * Created by logicp on 12/29/16.
 * Custom class to display RuvMenuItems
 */

public class RuvMenuAdapter extends ArrayAdapter {

    private Context mContext;
    private int layoutResourceId;
    private RuvMenuItem data[] = null;

    public RuvMenuAdapter(Context mContext, int layoutResourceId, RuvMenuItem[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView itemIcon = (ImageView) listItem.findViewById(R.id.side_item_icon);
        TextView itemTitle = (TextView) listItem.findViewById(R.id.side_item);

        RuvMenuItem folder = data[position];

        itemIcon.setImageResource(folder.icon);
        itemTitle.setText(folder.name);

        return listItem;
    }
}
