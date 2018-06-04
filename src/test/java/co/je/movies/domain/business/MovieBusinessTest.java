package co.je.movies.domain.business;

import co.je.movies.domain.entities.Movie;
import co.je.movies.persistence.daos.MovieDAO;
import co.je.movies.util.factories.MovieFactoryForTests;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class MovieBusinessTest {
    
    private Connection dbConnectionMock;
    private BasicDataSource dataSourceMock;
    private MovieDAO movieDAOMock;
    private MovieBusiness movieBusiness;
    
    @Before
    public void setUp() {
        try {
            dbConnectionMock = Mockito.mock(Connection.class);
            dataSourceMock = Mockito.mock(BasicDataSource.class);
            Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
            
            movieDAOMock = Mockito.mock(MovieDAO.class);
            movieBusiness = new MovieBusiness(dataSourceMock, movieDAOMock);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown() {
        dbConnectionMock = null;
        dataSourceMock = null;
        movieDAOMock = null;
        movieBusiness = null;
    }
    
    @Test
    public void testCreateMovie_OK() {
        try {
            Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
            String expectedImdbId = matrixMovie.getImdbId();
            Mockito.when(movieDAOMock.createMovie(dbConnectionMock, matrixMovie)).thenReturn(expectedImdbId);
            
            String imdbId = movieBusiness.createMovie(matrixMovie);
            assertNotNull(imdbId);
            assertEquals(expectedImdbId, imdbId);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testCreateMovie_NOK_SQLException() {
        try {
            Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
            Mockito.doThrow(SQLException.class).when(movieDAOMock).createMovie(dbConnectionMock, matrixMovie);
            
            movieBusiness.createMovie(matrixMovie);
            fail("Should send an IllegalStateException after catching the SQLException to break the system.");
        } catch (SQLException | IllegalStateException e) {
            assertNotNull(e);
            assertTrue(e instanceof IllegalStateException);
        }
    }

    @Test
    public void testGetMovieByImdbId_OK() throws SQLException {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        String imdbId = matrixMovie.getImdbId();

        Optional<Movie> expectedMovie = Optional.of(matrixMovie);
        Mockito.when(movieDAOMock.getMovieByImdbId(dbConnectionMock, imdbId)).thenReturn(expectedMovie);

        Optional<Movie> movieByImdbId = movieBusiness.getMovieByImdbId(imdbId);
        assertNotNull(movieByImdbId);
        assertEquals(0, matrixMovie.compareTo(movieByImdbId.get()));
    }

    @Test
    public void testGetMoviesByParams_OK() throws SQLException {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        Movie matrixReloadedMovie = MovieFactoryForTests.getMatrixReloadedMovie();
        List<Movie> expectedMovies = Arrays.asList(matrixMovie, matrixReloadedMovie);

        BigDecimal imdbRating = new BigDecimal("7.0");
        Mockito.when(
                movieDAOMock.getMoviesByParams(
                        dbConnectionMock,
                        "matrix",
                        100,
                        6,
                        imdbRating,
                        10000
                )
        ).thenReturn(expectedMovies);

        List<Movie> movies = movieBusiness.getMoviesByParams(
                "matrix",
                100,
                6,
                imdbRating,
                10000
        );
        assertNotNull(movies);
        assertEquals(2, movies.size());
    }

    @Test
    public void testUpdateMovie_OK() throws SQLException {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        Movie matrixReloadedMovie = MovieFactoryForTests.getMatrixReloadedMovie();
        String imdbId = matrixMovie.getImdbId();
        Movie expectedMovie = MovieFactoryForTests.getUpdatedMovie(imdbId, matrixReloadedMovie);
        Optional<Movie> optionalMovie = Optional.of(expectedMovie);
        Mockito.when(
                movieDAOMock.updateMovie(dbConnectionMock, imdbId, matrixReloadedMovie)
        ).thenReturn(optionalMovie);

        Optional<Movie> resultOptionalMovie = movieBusiness.updateMovie(imdbId, matrixReloadedMovie);
        assertTrue(resultOptionalMovie.isPresent());

        Movie movie = resultOptionalMovie.get();
        assertEquals(imdbId, movie.getImdbId());
        // The only attribute that doesn't match is the imdbID.
        assertEquals(1, matrixReloadedMovie.compareTo(expectedMovie));
    }

    @Test
    public void testDeleteMovie_OK() throws SQLException {
        Movie matrixMovie = MovieFactoryForTests.getMatrixMovie();
        String imdbId = matrixMovie.getImdbId();
        boolean movieWasDeleted = true;
        Mockito.when(
                movieDAOMock.deleteMovie(dbConnectionMock, imdbId)
        ).thenReturn(movieWasDeleted);

        boolean result = movieBusiness.deleteMovie(imdbId);
        assertEquals(movieWasDeleted, result);
    }
}