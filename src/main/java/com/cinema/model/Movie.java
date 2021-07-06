package com.cinema.model;

import lombok.*;
import javax.validation.constraints.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
public class Movie {
    private UUID id;
    @EqualsAndHashCode.Include
    @NotBlank(message = "Title is required and must contain at least 1 symbol")
    private String title;
    @EqualsAndHashCode.Include
    @NotNull(message = "Producer's name is required")
    @Pattern(regexp = "^([A-Z]{1}[a-z]+)( )([A-Z]{1}[a-z]+)$",
             message = "Required format for name: Xxxx Xxxxx")
    private String producerName;
    @NotNull(message = "Duration is required")
    private Duration duration;
    private Set<Genre> genres;
    @Min(value = 0, message = "Rating must not be less than 0")
    @Max(value = 10, message = "Rating must not be greater than 10")
    private double rating;

    public void addGenre(Genre genre){
        genres.add(genre);
    }

    public Movie createCopy(){
        return Movie.builder()
                .id(this.id)
                .title(this.title)
                .producerName(this.producerName)
                .duration(this.duration)
                .rating(this.rating)
                .genres(this.genres)
                .build();
    }

    public static MovieBuilder builder() {
        return new MovieBuilder();
    }


    public static class MovieBuilder {
        private UUID id = UUID.randomUUID();
        private String title;
        private String producerName;
        private Duration duration;
        private Set<Genre> genres = new HashSet<>();
        private double rating;

        MovieBuilder() {
        }

        public MovieBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public MovieBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MovieBuilder producerName(String producerName){
            this.producerName = producerName;
            return this;
        }

        public MovieBuilder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public MovieBuilder genres(Set<Genre> genres) {
            this.genres = genres;
            return this;
        }

        public MovieBuilder rating(double rating) {
            this.rating = rating;
            return this;
        }

        public Movie build() {
            return new Movie(id, title, producerName,
                             duration, genres, rating);
        }
    }
}
