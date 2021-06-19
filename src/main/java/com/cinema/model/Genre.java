package com.cinema.model;

import com.cinema.utility.validator.ValidatorUtility;
import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

    public void setName(String name) {
        this.name = name;
        ValidatorUtility.validateModel(this);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static GenreBuilder builder() {
        return new GenreBuilder();
    }


    public static class GenreBuilder {
        private final UUID id = UUID.randomUUID();
        private String name;
        private String description;

        GenreBuilder() {
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
            Genre genre = new Genre(id, name, description);
            ValidatorUtility.validateModel(genre);
            return genre;
        }

        public String toString() {
            return "Genre.GenreBuilder(id=" + this.id + ", "
                   + "name=" + this.name + ", "
                   + "description=" + this.description
                   + ")";
        }
    }
}

