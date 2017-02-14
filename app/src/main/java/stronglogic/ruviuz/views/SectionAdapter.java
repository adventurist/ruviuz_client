package stronglogic.ruviuz.views;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.Section;


/**
 * Created by logicp on 2/12/17.
 *Ruviuz Section Adapter
 **/

public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Activity mActivity;

    private ArrayList<Section> sectionList;

    private static final String TAG = "RuviuzSECTIONADAPTER";
    private final static int SEC_VIEW = 0;



    public SectionAdapter(Activity activity, ArrayList sectionList)   {
        super();
        this.mActivity = activity;
        this.sectionList = sectionList;
    }


    private static class SectionHolder extends RecyclerView.ViewHolder  {
        TextView sectionId, sectionLength, sectionWidth, sectionArea, emptyArea, emptyLabel;

        SectionHolder(View mView) {
            super(mView);
            sectionId = (TextView) mView.findViewById(R.id.sectionId);
            sectionLength = (TextView) mView.findViewById(R.id.sectionLength);
            sectionWidth = (TextView) mView.findViewById(R.id.sectionWidth);
            sectionArea = (TextView) mView.findViewById(R.id.sectionArea);
            emptyLabel = (TextView) mView.findViewById(R.id.emptyLabel);
            emptyArea = (TextView) mView.findViewById(R.id.emptyArea);
        }
    }

    
    @Override
    public int getItemViewType(int position)    {
        return SEC_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        if (viewType == SEC_VIEW) {
            return new SectionHolder(LayoutInflater.from(mActivity).inflate(R.layout.section_row,
                    parent, false));
        }
        return null;
    }


    @Override
    public int getItemCount()  {return this.sectionList.size();}

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Section section = sectionList.get(holder.getAdapterPosition());
        if (holder.getItemViewType() == SEC_VIEW) {
            final SectionHolder sectionHolder = (SectionHolder) holder;
            sectionHolder.sectionId.setText(String.valueOf(position + 1));
            sectionHolder.sectionLength.setText(String.valueOf(section.getLength()));
            sectionHolder.sectionWidth.setText(String.valueOf(section.getWidth()));
            sectionHolder.sectionArea.setText(String.valueOf(section.getLength() * section.getWidth()));
            if (section.getMissing() > 0) {
                sectionHolder.emptyArea.setVisibility(View.VISIBLE);
                sectionHolder.emptyLabel.setVisibility(View.VISIBLE);
                sectionHolder.emptyArea.setText(String.valueOf(section.getMissing()));
            }

        }
    }

}
