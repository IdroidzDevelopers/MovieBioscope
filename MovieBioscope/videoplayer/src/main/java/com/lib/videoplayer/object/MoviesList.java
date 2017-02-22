package com.lib.videoplayer.object;

import java.util.List;

/**
 * Created by aarokiax on 2/22/2017.
 */

public class MoviesList {
    private String language;
    private List<Movie> movies;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
