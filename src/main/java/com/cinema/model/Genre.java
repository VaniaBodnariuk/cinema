package com.cinema.model;

import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Genre {
    private UUID id;
    @EqualsAndHashCode.Include
    @NotNull(message = "Name is required")
    @Pattern(regexp = "^[A-Z]{1}[a-z -]{1,}$",
             message = "Length of name must be at least 2 letters. "
                       + "Name can contain letters, dashes, whitespaces. "
                       + "Name must start with uppercase")
    private String name;
    private String description;

    public Genre createCopy(){
        return Genre.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .build();
    }

    public static GenreBuilder builder() {
        return new GenreBuilder();
    }


    public static class GenreBuilder {
        private UUID id = UUID.randomUUID();
        private String name;
        private String description;

        GenreBuilder() {
        }

        public GenreBuilder id(UUID id){
            this.id = id;
            return this;
        }

        public GenreBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GenreBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Genre build() {
            return new Genre(id, name, description);
        }
    }
}

