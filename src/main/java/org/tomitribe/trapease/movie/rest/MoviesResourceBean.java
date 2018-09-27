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

import io.swagger.v3.oas.annotations.Parameter;
import org.tomitribe.trapease.movie.model.BulkMovieResult;
import org.tomitribe.trapease.movie.model.CreateMovie;
import org.tomitribe.trapease.movie.model.Movie;
import org.tomitribe.trapease.movie.model.MovieFilter;
import org.tomitribe.trapease.movie.model.MovieResult;
import org.tomitribe.trapease.movie.model.UpdateMovie;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ApplicationScoped
public class MoviesResourceBean implements MoviesResource {
    private AtomicInteger idGenerator = new AtomicInteger(0);
    private Map<String, Movie> moviesRepository = new ConcurrentHashMap<>();

    @Context
    private UriInfo uriInfo;

    @Override
    public Response bulkCreate(@Parameter(description = "Set of CreateMovie to create", required = true) List<CreateMovie> movies) {

        if (movies != null) {
            List<Movie> newMovies = movies.stream().map(c -> {
                Movie m = Movie.builder()
                        .id(idGenerator.incrementAndGet() + "")
                        .title(c.getTitle())
                        .director(c.getDirector())
                        .genre(c.getGenre())
                        .year(c.getYear())
                        .rating(c.getRating())
                        .build();
                return m;
            }).collect(Collectors.toList());

            newMovies.forEach(m -> {
                moviesRepository.put(m.getId(), m);
            });
        }
        return Response.ok().entity(new BulkMovieResult(null)).build();
    }

    @Override
    public Response bulkUpdate(@Parameter(description = "Set of UpdateMovie to update", required = true) List<UpdateMovie> movies) {
        return null;
    }

    @Override
    public Response bulkDelete(@Parameter(description = "Set of Movie ids to delete", required = true) List<String> ids) {
        if(ids != null){
            ids.forEach(id -> {
                moviesRepository.remove(id);
            });
        }
        return Response.ok().entity(new BulkMovieResult(null)).build();
    }

    @Override
    public Response readAll() {
        MovieResult movies = MovieResult.builder()
                .items(moviesRepository.values())
                .total(Long.valueOf(moviesRepository.values().size()))
                .filters(MovieFilter.builder().title("anc").build())
                .build();
        return Response.ok().entity(movies).build();
    }
}
