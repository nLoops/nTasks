package com.nloops.ntasks.data;


import android.provider.BaseColumns;

/**
 * The Contract that defines Database Tables content
 */
public class TasksDBContract {

    // to prevent to instantiating the class.
    private TasksDBContract() {
    }

    /**
     * inner class that contains Task Table columns
     */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_DATE = "duedate";
        public static final String COLUMN_NAME_PRIORTY = "priorty";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_COMPLETE = "is_complete";
    }

    /**
     * inner class that contains related Task TODOs table columns
     */
    public static abstract class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_TODO = "txttodo";

    }
}
