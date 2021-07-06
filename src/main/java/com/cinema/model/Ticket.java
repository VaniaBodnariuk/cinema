package com.cinema.model;

import com.opencsv.bean.CsvRecurse;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {
    @EqualsAndHashCode.Include
    private UUID id;
    @NotNull(message = "Movie is required")
    @CsvRecurse
    private Movie movie;
    @NotNull(message = "User is required")
    @CsvRecurse
    private User user;
    private LocalDateTime date;
    @NotNull(message = "Price is required")
    private Double price;

    public Ticket createCopy(){
        return Ticket.builder()
                .id(this.id)
                .user(this.user)
                .movie(this.movie)
                .price(this.price)
                .date(this.date)
                .build();
    }

    public static TicketBuilder builder() {
        return new TicketBuilder();
    }


    public static class TicketBuilder {
        private UUID id = UUID.randomUUID();
        private Movie movie;
        private User user;
        private LocalDateTime date = LocalDateTime.now();
        private Double price;

        TicketBuilder() {
        }

        public TicketBuilder id(UUID id){
            this.id = id;
            return this;
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
            return new Ticket(id, movie, user, date, price);
        }
    }
}
