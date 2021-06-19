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
public class User {
    @EqualsAndHashCode.Include
    private UUID id;
    @NotNull(message = "Name is required")
    @Pattern(regexp = "^([A-Z]{1}[a-z]+)( )([A-Z]{1}[a-z]+)$",
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

    public void setName(String name) {
        this.name = name;
        ValidatorUtility.validateModel(this);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        ValidatorUtility.validateModel(this);
    }


    public static class UserBuilder {
        private final UUID id = UUID.randomUUID();
        private String name;
        private String phone;

        UserBuilder() {
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
            User user = new User(id, name, phone);
            ValidatorUtility.validateModel(user);
            return user;
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", "
                   + "name=" + this.name + ", "
                   + "phone=" + this.phone
                   + ")";
        }
    }
}
