package com.cinema.model;

import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private UUID id;
    @NotNull(message = "Name is required")
    @Pattern(regexp = "^([A-Z]{1}[a-z0-9]+)( )([A-Z]{1}[a-z0-9]+)$",
             message = "Required format for name: Xxxx Xxxxx")
    private String name;
    @Pattern(regexp = "^0?(67|68|96|97|98|50|66|95|99|63|73|93|91|92|94)"
                      + "\\s\\d{3}\\s\\d{4}$",
             message = "Required format for phone number: 0UU XXX XXXX, "
                       + "where UU - ukrainian mobile operators codes")
    private String phone;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public User createCopy(){
        return User.builder()
                .id(this.id)
                .name(this.name)
                .phone(this.phone)
                .build();
    }


    public static class UserBuilder {
        private UUID id = UUID.randomUUID();
        private String name;
        private String phone;

        UserBuilder() {
        }

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build() {
            return new User(id, name, phone);
        }
    }
}
