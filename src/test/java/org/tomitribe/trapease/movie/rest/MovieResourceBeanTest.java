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
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.trapease.movie.model.CreateMovie;
import org.tomitribe.trapease.movie.model.Movie;
import org.tomitribe.trapease.movie.model.UpdateMovie;
import org.tomitribe.trapease.movie.rest.client.MovieClient;
import org.tomitribe.trapease.movie.rest.client.base.ClientConfiguration;

import javax.ws.rs.core.Response;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class MovieResourceBeanTest {
    @Deployment(testable = false)
    public static WebArchive webApp() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(Movie.class)
                .addClass(CreateMovie.class)
                .addClass(UpdateMovie.class)
                .addClass(MovieResource.class)
                .addClass(MovieResourceBean.class);
    }

    @Test
    public void testMovies(final @ArquillianResource URL base) throws Exception {
        final CreateMovie createMovie =
                CreateMovie.builder()
                        .title("The Terminator")
                        .director("James Cameron")
                        .genre("Action")
                        .year(1984)
                        .rating(8)
                        .build();

        MovieClient movieClient = new MovieClient(ClientConfiguration.builder().url(base).verbose(true).build());
        Movie movie = movieClient.movie().create(createMovie);

        UpdateMovie updateMovie = movie.toUpdate().year(1985).build();
        Movie updatedMovie = movieClient.movie().update(movie.getId(), updateMovie);

        assertEquals(1985, updatedMovie.getYear());

        Response delete = movieClient.movie().delete(updatedMovie.getId());

        assertEquals(204, delete.getStatus());

        movieClient.movie().read(updatedMovie.getId());

    }
}
