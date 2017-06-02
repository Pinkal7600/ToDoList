package com.pinkal.todolist.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Pinkal Daliya on 19-Oct-16.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//
//        int appWidgetId = intent.getIntExtra(
//                AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new WidgetListAdapter(this.getApplicationContext(), intent));
    }
}
