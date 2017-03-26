package stronglogic.ruviuz.util;

import android.util.Log;
import android.widget.Filter;

import java.math.BigDecimal;
import java.util.ArrayList;

import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.views.RuvAdapter;

/**
 * Created by logicp on 1/19/17.
 * Making RuvFilter public to expand its functionality
 */

public class RuvFilter extends Filter {

    private final static String TAG = "RuviuzRUVFILTER";

    public enum filterType { CUSTOMER, ADDRESS, PRICE }

    private ArrayList<Roof> originalList;
    private ArrayList<Roof> filteredList;

    private RuvAdapter adapter;

    private filterType chosenType;

    private boolean multiSearch;

    private ArrayList<filterType> chosenTypes;

//    public RuvFilter (IndexAdapter adapter, ArrayList<Roof> originalList) {
//        super();
//        this.originalList = new ArrayList<>(originalList);
//        this.filteredList = new ArrayList<>();
//        this.adapter = adapter;
//    }

    public RuvFilter (RuvAdapter adapter, ArrayList<Roof> originalList) {
        super();
        this.originalList = new ArrayList<>(originalList);
        this.filteredList = new ArrayList<>();
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        final FilterResults results = new FilterResults();
        filteredList.clear();
        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().trim();
            if (chosenType == filterType.ADDRESS) {
                for (final Roof ruv : originalList) {
                    if (ruv.getAddress().contains(filterPattern)) {
                        filteredList.add(ruv);
                    }
                }
            } else if (chosenType == filterType.CUSTOMER) {
                for (final Roof ruv : originalList) {
                    if (ruv.getCustomerName().contains(filterPattern)) {
                        filteredList.add(ruv);
                    }
                }
            } else if (chosenType == filterType.PRICE) {
                for (final Roof ruv : originalList) {
                    if (ruv.getPrice().compareTo(BigDecimal.valueOf(Float.valueOf(filterPattern))) == 1) {
                        filteredList.add(ruv);
                    }
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Log.d("RuviuzINDEXADAPTER", "ruvListSize::" + adapter.ruvList.size());
        adapter.ruvList.clear();
        if (results.values != null) {
            adapter.ruvList.addAll((ArrayList<Roof>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

    public boolean setType(filterType type) {
        if (type == filterType.ADDRESS || type == filterType.CUSTOMER || type == filterType.PRICE) {
            this.chosenType = type;
            Log.d(TAG, "Type set to " + String.valueOf(type));
            return true;
        } else {
            Log.d(TAG, "Attempting wrong filter type");
            return false;
        }
    }

    public void setMultiSearch(boolean bool) {
        this.multiSearch = bool;
    }

    public boolean isMulti() {
        return this.multiSearch;
    }

    public void addSearchType(filterType type) {
        if (this.chosenTypes == null) this.chosenTypes = new ArrayList<filterType>();

        if (type == filterType.ADDRESS || type == filterType.CUSTOMER || type == filterType.PRICE) {
            if (!chosenTypes.contains(type)) chosenTypes.add(type);
        }
    }
}

