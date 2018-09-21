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
package org.tomitribe.trapease.movie.model;

import org.tomitribe.api.Filter;
import org.tomitribe.api.Model;
import org.tomitribe.api.Resource;

@Model
@Resource
class MovieModel {
    @Model(id = true, operation = Model.Operation.READ)
    private String id;
    @Model(filter = @Filter(name = "title", multiple = false))
    private String title;
    private String director;
    private String genre;
    private int year;
    private int rating;
}
