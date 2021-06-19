package com.cinema.model;

import com.cinema.utility.validator.ValidatorUtility;
import lombok.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {
    @EqualsAndHashCode.Include
    private UUID id;
    @NotNull(message = "Movie is required")
    private Movie movie;
    @NotNull(message = "User is required")
    private User user;
    @FutureOrPresent
    private LocalDateTime date = LocalDateTime.now();
    @NotNull(message = "Price is required")
    private Double price;

    public void setMovie(Movie movie) {
        this.movie = movie;
        ValidatorUtility.validateModel(movie);
    }

    public void setUser(User user) {
        this.user = user;
        ValidatorUtility.validateModel(this);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
        ValidatorUtility.validateModel(this);
    }

    public void setPrice(Double price) {
        this.price = price;
        ValidatorUtility.validateModel(this);
    }

    public static TicketBuilder builder() {
        return new TicketBuilder();
    }


    public static class TicketBuilder {
        private final UUID id = UUID.randomUUID();
        private Movie movie;
        private User user;
        private LocalDateTime date;
        private Double price;

        TicketBuilder() {
        }

        public TicketBuilder movie(Movie movie) {
            this.movie = movie;
            return this;
        }

        public TicketBuilder user(User user) {
            this.user = user;
            return this;
        }

        public TicketBuilder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public TicketBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public Ticket build() {
            Ticket ticket = new Ticket(id, movie, user, date, price);
            ValidatorUtility.validateModel(ticket);
            return ticket;
        }

        public String toString() {
            return "Ticket.TicketBuilder(id=" + this.id + ", "
                   + "movie=" + this.movie + ", "
                   + "user=" + this.user + ", "
                   + "date=" + this.date + ", "
                   + "price=" + this.price + ")";
        }
    }
}
