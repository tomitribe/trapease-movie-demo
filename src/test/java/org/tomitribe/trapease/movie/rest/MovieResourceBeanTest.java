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
import org.tomitribe.trapease.movie.model.UpdateMovie;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.URL;

import static javax.ws.rs.client.Entity.json;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class MovieResourceBeanTest {
    @Deployment
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
        final Client client = ClientBuilder.newBuilder().build();

        final CreateMovie createMovie =
                CreateMovie.builder()
                           .title("The Terminator")
                           .director("James Cameron")
                           .genre("Action")
                           .year(1984)
                           .rating(8)
                           .build();

        final Response createResponse =
                client.target(base.toURI())
                      .path("movie")
                      .request()
                      .post(json(createMovie));
        assertEquals(201, createResponse.getStatus());
        assertNotNull(createResponse.getLocation());

        final Movie movie =
                ClientBuilder.newBuilder().build().target(createResponse.getLocation()).request().get(Movie.class);
        assertNotNull(movie);
        assertEquals("The Terminator", movie.getTitle());
        assertEquals("James Cameron", movie.getDirector());
        assertEquals("Action", movie.getGenre());
        assertEquals(1984, movie.getYear());
        assertEquals(8, movie.getRating());

        final Response updateResponse =
                client.target(base.toURI())
                      .path("movie/{id}")
                      .resolveTemplate("id", movie.getId())
                      .request()
                      .put(json(movie.toUpdate().rating(9).build()));
        assertEquals(200, updateResponse.getStatus());

        final Movie updatedMovie = updateResponse.readEntity(Movie.class);
        assertNotNull(updatedMovie);
        assertEquals(9, updatedMovie.getRating());

        final Response deleteResponse =
                client.target(base.toURI())
                      .path("movie/{id}")
                      .resolveTemplate("id", movie.getId())
                      .request()
                      .delete();
        assertEquals(204, deleteResponse.getStatus());

        assertEquals(404, client.target(base.toURI())
                                .path("movie{id}")
                                .resolveTemplate("id", movie.getId())
                                .request()
                                .get()
                                .getStatus());
    }
}
