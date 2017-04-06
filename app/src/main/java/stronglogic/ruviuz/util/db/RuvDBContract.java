package stronglogic.ruviuz.util.db;

import android.provider.BaseColumns;

/**
 * Created by logicp on 3/31/17.
 * Database contract to handle autocompletion in RuvSearches
 */

public class RuvDBContract {

    private RuvDBContract() {}


    public static class RuvEntry implements BaseColumns {
        public static final String TABLE_NAME = "ruv_search";
        public static final String _id = "_id";
        public static final String TYPE = "type";
        public static final String PATTERN = "pattern";
    }
}
