package co.je.movies.api.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jersey.validation.ConstraintViolationExceptionMapper;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import co.je.movies.domain.business.MovieBusiness;
import co.je.movies.domain.entities.Movie;
import co.je.movies.util.factories.MovieFactoryForTests;
import co.je.movies.util.factories.ObjectMapperFactoryForTests;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MovieResourceTest {

    private static final MovieBusiness movieBusinessMock = Mockito.mock(MovieBusiness.class);
    private static final MovieResource movieResource = new MovieResource(movieBusinessMock);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(movieResource)
            .addProvider(ConstraintViolationExceptionMapper.class)
            .setMapper(ObjectMapperFactoryForTests.getConfiguredObjectMapper()).build();

    private void createMovie(Movie movie) {
        String createUri = "/movies";
        Mockito.when(movieBusinessMock.createMovie(movie)).thenReturn(movie.getImdbId());
        Response createResponse = resources.client().target(createUri).request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).post(Entity.entity(movie, MediaType.APPLICATION_JSON));

        assertNotNull(createResponse);
        assertEquals(201, createResponse.getStatus());
    }

    @Test
    public void testCreateMovie_OK() {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        createMovie(matrixMovie);
    }

    @Test
    public void testCreateMovie_NOK_incompleteMovie() {
        String uri = "/movies";
        Movie matrixMovie = MovieFactoryForTests.getNotValidMovie();
        Response response = resources.client().target(uri).request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).post(Entity.entity(matrixMovie, MediaType.APPLICATION_JSON));

        assertNotNull(response);

        int status = response.getStatus();
        assertEquals(422, status);
    }

    @Test
    public void testGetMovieByImdbId_OK() {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        createMovie(matrixMovie);

        Optional<Movie> optionalMovie = Optional.of(matrixMovie);
        Mockito.when(movieBusinessMock.getMovieByImdbId(matrixMovie.getImdbId())).thenReturn(optionalMovie);

        String getUri = "/movies/" + matrixMovie.getImdbId();
        Response getResponse = resources.client().target(getUri).request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).get();

        assertNotNull(getResponse);
        assertEquals(200, getResponse.getStatus());

        Movie movie = getResponse.readEntity(Movie.class);
        assertEquals(0, matrixMovie.compareTo(movie));
    }

    @Test
    public void testGetMoviesByParams_OK() {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        createMovie(matrixMovie);
        Movie matrixReloadedMovie = MovieFactoryForTests.getMatrixReloadedMovie();
        createMovie(matrixReloadedMovie);

        String title = "matrix";
        int runtimeInMinutes = 100;
        int metascore = 6;
        BigDecimal imdbRating = new BigDecimal("7.0");
        long imdbVotes = 10000;
        List<Movie> expectedMovies = Arrays.asList(matrixMovie, matrixReloadedMovie);
        Mockito.when(
                movieBusinessMock.getMoviesByParams(title, runtimeInMinutes, metascore, imdbRating, imdbVotes)
        ).thenReturn(expectedMovies);

        String getUri = "/movies";
        Response getResponse = resources.client().target(getUri)
                .queryParam("title", title)
                .queryParam("runtimeInMinutes", runtimeInMinutes)
                .queryParam("metascore", metascore)
                .queryParam("imdbRating", imdbRating)
                .queryParam("imdbVotes", imdbVotes)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).get();

        assertNotNull(getResponse);
        assertEquals(200, getResponse.getStatus());

        Movie[] movies = getResponse.readEntity(Movie[].class);
        assertEquals(2, movies.length);
    }
}