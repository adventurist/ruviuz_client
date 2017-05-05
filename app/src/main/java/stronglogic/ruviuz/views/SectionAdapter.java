package stronglogic.ruviuz.views;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;
import stronglogic.ruviuz.content.Section;


/**
 * Created by logicp on 2/12/17.
 *Ruviuz Section Adapter
 **/

public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;

    private ArrayList<Section> sectionList;

    private static final String TAG = "RuviuzSECTIONADAPTER";
    private final static int SEC_VIEW = 0;
    private final static int SEC_HVIEW = 1;
    private final static int SEC_VIEW_EDIT = 2;
    private final static int SEC_HVIEW_EDIT = 3;

    private boolean editMode;

    public SectionAdapter(Activity activity, ArrayList<Section> sectionList)   {
        super();
        this.mActivity = activity;
        this.sectionList = sectionList;
    }


    public SectionAdapter(Activity activity, ArrayList<Section> sectionList, boolean editMode)   {
        super();
        this.mActivity = activity;
        this.sectionList = sectionList;
        this.editMode = editMode;
    }



    public static class SectionHolder extends RecyclerView.ViewHolder  {
        TextView sectionId, sectionType, sectionLength, sectionWidth, sectionTopWidth, sectionArea, sectionSlope, emptyArea, emptyLabel, emptyFt2, emptyWidth, emptyLength, emptyTypeLabel, emptyType;

        Button secEditBtn;

        SectionHolder(View mView) {

            super(mView);
            sectionId = (TextView) mView.findViewById(R.id.sectionId);
            sectionType = (TextView) mView.findViewById(R.id.sectionType);
            sectionLength = (TextView) mView.findViewById(R.id.sectionLength);
            sectionWidth = (TextView) mView.findViewById(R.id.sectionWidth);
            sectionTopWidth = (TextView) mView.findViewById(R.id.sectionTopWidth);
            sectionSlope = (TextView) mView.findViewById(R.id.sectionSlope);
            sectionArea = (TextView) mView.findViewById(R.id.sectionArea);
            emptyLabel = (TextView) mView.findViewById(R.id.emptyLabel);
            emptyArea = (TextView) mView.findViewById(R.id.emptyArea);
            emptyFt2 = (TextView) mView.findViewById(R.id.emptFt2);
            emptyLength = (TextView) mView.findViewById(R.id.sectionEmptyLength);
            emptyWidth = (TextView) mView.findViewById(R.id.sectionEmptyWidth);
            emptyTypeLabel = (TextView) mView.findViewById(R.id.emptyTypeLabel);
            emptyType = (TextView) mView.findViewById(R.id.emptyType);
            secEditBtn = (Button) mView.findViewById(R.id.sectionEditBtn);

        }
    }

    
    @Override
    public int getItemViewType(int position)    {

        if (mActivity instanceof RviewActivity) {
            return !SectionAdapter.this.editMode ? SEC_HVIEW : SEC_HVIEW_EDIT;
        } else {
            return !SectionAdapter.this.editMode ? SEC_VIEW : SEC_VIEW_EDIT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        switch (viewType) {

            case SEC_HVIEW:
                return new SectionHolder(LayoutInflater.from(mActivity).inflate(R.layout.section_hrow,
                        parent, false));

            case SEC_VIEW:
                return new SectionHolder(LayoutInflater.from(mActivity).inflate(R.layout.section_row,
                        parent, false));

            case SEC_HVIEW_EDIT:
                return new SectionHolder(LayoutInflater.from(mActivity).inflate(R.layout.section_hrow_edit,

                        parent, false));

            case SEC_VIEW_EDIT:
                return new SectionHolder(LayoutInflater.from(mActivity).inflate(R.layout.section_row_edit,
                        parent, false));

        }

        return null;
    }


    @Override
    public int getItemCount()  { return this.sectionList.size(); }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Section section = sectionList.get(holder.getAdapterPosition());

        final SectionHolder sectionHolder = (SectionHolder) holder;
        sectionHolder.sectionId.setText(String.valueOf(position + 1));
        sectionHolder.sectionType.setText(section.getSectionType());
        sectionHolder.sectionLength.setText(String.valueOf(section.getLength()));
        sectionHolder.sectionWidth.setText(String.valueOf(section.getWidth()));
        sectionHolder.sectionTopWidth.setText(String.valueOf(section.getTopWidth()));
        sectionHolder.sectionSlope.setText(String.valueOf(section.getSlope()));
        sectionHolder.sectionArea.setText(String.valueOf(section.getLength() * section.getWidth()));

        if (section.getMissing() > 0) {

            sectionHolder.emptyArea.setVisibility(View.VISIBLE);
            sectionHolder.emptyLabel.setVisibility(View.VISIBLE);
            sectionHolder.emptyFt2.setVisibility(View.VISIBLE);
            sectionHolder.emptyArea.setText(String.valueOf(section.getMissing()));
            sectionHolder.emptyTypeLabel.setVisibility(View.VISIBLE);

            if (section.getEmptyLength() > 0 && section.getEmptyWidth() > 0) {
                sectionHolder.emptyLength.setVisibility(View.VISIBLE);
                sectionHolder.emptyWidth.setVisibility(View.VISIBLE);
                sectionHolder.emptyLength.setText(String.valueOf(section.getEmptyLength()));
                sectionHolder.emptyWidth.setText(String.valueOf(section.getEmptyWidth()));
            }

            sectionHolder.emptyType.setVisibility(View.VISIBLE);
            sectionHolder.emptyType.setText((section.getEmptyType()));
        }

        if (SectionAdapter.this.editMode) {

            if (mActivity instanceof RviewActivity) {

                sectionHolder.secEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ( (RviewActivity) mActivity).editSection(section);
                    }
                });
            }
        }

    }

    public void swapData(ArrayList<Section> list) {
        this.sectionList.clear();
        this.sectionList.addAll(list);
    }

}
