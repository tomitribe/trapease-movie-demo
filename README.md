# Trapease Demo Project

Trapease is a code generator tool that helps you generate model classes, rest resources, client code, documentation and 
even a command line client for your REST API.

## Model

Trapease uses Java code to generate more Java code. For a REST API Model, Trapease generates different objects for each 
REST operation, Read, Create and Update.

Create a simple class to use as template for Trapease. This class is not required to be used in the API. Trapease will 
generate specific objects with the information gathered from this class: 

```java
@Model
@Resource
class MovieModel {
    @Model(id = true, operation = Model.Operation.READ)
    private String id;
    private String title;
    private String director;
    private String genre;
    private int year;
    private int rating;
}
``` 

The ```@Model``` annotation tells Trapeasy that it should look into this Java source and generate a Model from it. By 
default the following classes are generated:

* Movie (for Get)
* CreateMovie (for Post)
* UpdateMovie (for Put)

You can control the generation by specifying the ```operation``` per class or field. A field marked with 
```Model.Operation.READ``` will only be generated for the Read version of the class.

## Resources

To generate REST endpoints Trapease looks for the ```@Resource``` annotation in the Model classes. It will generate a 
REST endpoint Interface with methods for GET, POST, PUT and DELETE.

## Setup

To setup Trapease, you just need to apply the Maven plugin to perform the code generation:

```xml
<plugin>
<groupId>org.tomitribe</groupId>
<artifactId>trapease-maven-plugin</artifactId>
<version>${version.trapease}</version>
<configuration>
  <modelPackage>org.tomitribe.trapease.movie.model</modelPackage>
  <resourcePackage>org.tomitribe.trapease.movie.rest</resourcePackage>
</configuration>
<executions>
  <execution>
    <phase>generate-sources</phase>
    <goals>
      <goal>generate</goal>
    </goals>
  </execution>
</executions>
</plugin>
``` 

And point the plugin property ```modelPackage``` to the package where the model classes reside. The 
```resourcePackage``` is the package used by Trapease to generate the REST Resources.

To be able to use the Trapeasy API, namely the ```@Model``` and ```@Resouce``` annotations, you also need to add the 
following dependency to your project:

```xml
<dependency>
  <groupId>org.tomitribe</groupId>
  <artifactId>trapease-api</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Run the Demo

Build the demo as you would usually build any Maven project with ```mvn clean install```. Trapeasy generated code sources can be found in the ```target\generated-sources``` folder. 

The project contains an inplementation of the ```MovieResource``` REST Resource and a test class using Arquillian that calls each of the methods.
