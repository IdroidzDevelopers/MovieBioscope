package com.lib.location.util;

/**
 * Created by aarokiax on 2/15/2017.
 */

public interface LocationInterface {
    String MATRIX_API_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&departure_time=now" +
            "&traffic_model=best_guess&mode=driving";
    String ORIGIN_KEY = "&origins=";
    String DESTINATION_KEY = "&destinations=";
    String API_KEY = "&key=";
    String DISTANCE_MATRIX_API_KEY = "AIzaSyCRitYQF8rF1DG-Lo3PfAjBvjIqbw9PMbw";

    int HANDLE_LOCATION_INFO = 1;
    int HANDLE_JOURNEY_INFO = 2;

    String STATUS_OK_KEY = "OK";

    int LOCATION_INTERVAL=5000;
    int FASTEST_INTERVAL=5000;
    float MINIMUM_DISPLACEMENT=100.0f;

}
