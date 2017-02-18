package com.lib.location.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lib.location.R;
import com.lib.route.objects.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarokiax on 2/18/2017.
 */

public class RouteAdapter extends ArrayAdapter<Route> {

    // declaring our ArrayList of items
    private List<Route> routes;

    public RouteAdapter(Context context, int textViewResourceId, List<Route> routes) {
        super(context, textViewResourceId, routes);
        this.routes = routes;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.route_row, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Route i = routes.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView routeText = (TextView) v.findViewById(R.id.route_text);


            // check to see if each individual textview is null.
            // if not, assign some text!

            if (routeText != null){
                routeText.setText(i.getmRouteSource()+" - "+i.getmRouteDestination());
            }
        }

        // the view must be returned to our activity
        return v;

    }
}
