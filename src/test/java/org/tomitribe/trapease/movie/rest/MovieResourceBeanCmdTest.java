/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.trapease.movie.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.trapease.movie.model.CreateMovie;
import org.tomitribe.trapease.movie.model.Movie;
import org.tomitribe.trapease.movie.model.Movies;
import org.tomitribe.trapease.movie.model.UpdateMovie;
import org.tomitribe.trapease.movie.rest.client.MovieClient;
import org.tomitribe.trapease.movie.rest.client.base.ClientConfiguration;
import org.tomitribe.trapease.movie.rest.client.base.EntityNotFoundException;
import org.tomitribe.trapease.movie.rest.cmd.base.TrapeaseCli;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class MovieResourceBeanCmdTest {
    @Deployment
    public static WebArchive webApp() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(Movie.class)
                .addClass(CreateMovie.class)
                .addClass(UpdateMovie.class)
                .addClass(MovieResource.class)
                .addClass(MovieResourceBean.class)
                .addClass(MoviesResource.class)
                .addClass(MoviesResourceBean.class);
    }

    @Test
    public void testMovieResource(final @ArquillianResource URL base) throws Exception {
        cmd("movie create --title \"The Terminator\" --director \"James Cameron\" --genre Action --year 1084 --rating 8", base);

        MovieClient movieClient = new MovieClient(ClientConfiguration.builder().url(base).verbose(true).build());
        Movie readMovie = movieClient.movie().read("1");
        assertEquals("The Terminator", readMovie.getTitle());

        cmd("movie update 1 --title \"The Terminator\" --director \"James Cameron\" --genre Action --year 1085 --rating 8", base);

        cmd("movie read 1", base);

        cmd("movie delete 1", base);

        try {
            movieClient.movie().read("1");
            fail();
        } catch (final EntityNotFoundException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testMoviesResource(final @ArquillianResource URL base) throws Exception {
        cmd("movies bulk-create --title \"The Terminator\" --director \"James Cameron\" --genre Action --year 1084 --rating 8", base);

        MovieClient movieClient = new MovieClient(ClientConfiguration.builder().url(base).verbose(true).build());
        Movies movies = movieClient.movies().readAll();
        assertEquals(new Long(1), movies.getTotal());

//        cmd("movies bulk-update 1 --title \"The Terminator\" --director \"James Cameron\" --genre Action --year 1085 --rating 8", base);

        cmd("movie readAll", base);

//        cmd("movie bulk-delete 1", base);

    }

    private static void cmd(final String cmd, final URL url) {
        List<String> params = new ArrayList<>(Arrays.asList(cmd.split(" (?=(([^'\"]*['\"]){2})*[^'\"]*$)")));
        params = params.stream().map(p -> p.replaceAll("\"", "")).collect(Collectors.toList());
        params.add(0, "--url");
        params.add(1, url.toString());
        System.out.println("trapease " + params.stream().collect(Collectors.joining(" ")));
        TrapeaseCli.main(params.toArray(new String[]{}));
    }
}
