package com.pinkal.todolist.utils;

/**
 * Created by Pinkal Daliya on 06-Oct-16.
 */

public interface Constant {

    //Database
    String DATABASE_NAME = "todo_category";
    int DATABASE_VERSION = 1;

    //table category
    String TABLE_CATEGORY = "category";
    String CATEGORY_ID = "id";
    String CATEGORY_NAME = "name";

    //table task
    String TABLE_TASK = "task";
    String TASK_TITLE = "task_title";
    String TASK_ID = "id";
    String TASK_TASK = "task";
    String TASK_DATE = "date";
    String TASK_TIME = "time";
    String TASK_CATEGORY_ID = "categoryId";
    String TASK_FLAG = "flag";

    String ROW_ID = "row_id";
    String NOTIFICATION = "notify";
    String WIDGET = "widget";
}
