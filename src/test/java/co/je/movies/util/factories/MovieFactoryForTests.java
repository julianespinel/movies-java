package co.je.movies.util.factories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.je.movies.domain.entities.FilmRating;
import co.je.movies.domain.entities.Movie;

public class MovieFactoryForTests {

    public static Movie getMatrixMovie() {

        String imdbId = "tt0133093";
        String title = "The Matrix";
        int runtimeInMinutes = 136;
        LocalDateTime releaseDate = LocalDateTime.of(1999, 03, 31, 00, 00);
        FilmRating filmRating = FilmRating.R;
        String genre = "Action, Sci-Fi";
        String director = "The Wachowski brothers";
        String plot = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.";
        int metascore = 73;
        BigDecimal imdbRating = new BigDecimal("8.7");
        long imdbVotes = 1023621;

        return new Movie(imdbId, title, runtimeInMinutes, releaseDate, filmRating, genre, director, plot, metascore, imdbRating, imdbVotes);
    }

    public static Movie getMatrixReloadedMovie() {

        String imdbId = "tt0234215";
        String title = "The Matrix Reloaded";
        int runtimeInMinutes = 138;
        LocalDateTime releaseDate = LocalDateTime.of(2003, 05, 15, 00, 00);
        FilmRating filmRating = FilmRating.R;
        String genre = "Action, Sci-Fi";
        String director = "The Wachowski brothers";
        String plot = "Neo and the rebel leaders estimate that they have 72 hours until 250,000 probes discover Zion and " +
                "destroy it and its inhabitants. During this, Neo must decide how he can save Trinity from a dark fate in his dreams.";
        int metascore = 62;
        BigDecimal imdbRating = new BigDecimal("7.2");
        long imdbVotes = 458720;

        return new Movie(imdbId, title, runtimeInMinutes, releaseDate, filmRating, genre, director, plot, metascore, imdbRating, imdbVotes);
    }
    
    public static Movie getNotValidMovie() {

        String imdbId = "tt0133093";
        String title = "";
        int runtimeInMinutes = -136;
        LocalDateTime releaseDate = LocalDateTime.of(1999, 03, 31, 00, 00);
        FilmRating filmRating = FilmRating.R;
        String genre = "Action, Sci-Fi";
        String director = "The Wachowski brothers";
        String plot = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.";
        int metascore = 73;
        BigDecimal imdbRating = new BigDecimal("8.7");
        long imdbVotes = 1023621;

        return new Movie(imdbId, title, runtimeInMinutes, releaseDate, filmRating, genre, director, plot, metascore, imdbRating, imdbVotes);
    }

    public static Movie getUpdatedMovie(String originalImdbId, Movie movieToUpdate) {
        return new Movie(
                originalImdbId,
                movieToUpdate.getTitle(),
                movieToUpdate.getRuntimeInMinutes(),
                movieToUpdate.getReleaseDate(),
                movieToUpdate.getFilmRating(),
                movieToUpdate.getGenre(),
                movieToUpdate.getDirector(),
                movieToUpdate.getPlot(),
                movieToUpdate.getMetascore(),
                movieToUpdate.getImdbRating(),
                movieToUpdate.getImdbVotes()
        );
    }
}