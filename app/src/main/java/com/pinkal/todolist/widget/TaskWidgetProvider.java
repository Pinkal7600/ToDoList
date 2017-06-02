package com.pinkal.todolist.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.pinkal.todolist.R;
import com.pinkal.todolist.activity.AddUpdateActivity;

/**
 * Created by Pinkal Daliya on 19-Oct-16.
 */
public class TaskWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context, TaskWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);


//        for (int i = 0; i < appWidgetIds.length; i++) {
        for (int widgetId : allWidgetIds) {

            Log.e("appId i *-*-> ", String.valueOf(widgetId));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_tasklist_layout);

            Intent mIntent = new Intent(context, WidgetService.class);

            mIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            mIntent.setData(Uri.parse(mIntent.toUri(Intent.URI_INTENT_SCHEME)));


            widget.setRemoteAdapter(widgetId, R.id.widgetListview, mIntent);

            //click event
            Intent clickIntent = new Intent(context, AddUpdateActivity.class);
            PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.widgetListview, clickPI);

            //btn add click
            Intent addIntent = new Intent(context, AddUpdateActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 1, addIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setOnClickPendingIntent(R.id.widgetBtnAdd, clickPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
