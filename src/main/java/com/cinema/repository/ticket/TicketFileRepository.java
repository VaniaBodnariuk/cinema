package com.cinema.repository.ticket;

import com.cinema.model.Ticket;
import com.cinema.repository.BasicFileRepository;
import java.util.UUID;

public interface TicketFileRepository extends BasicFileRepository<Ticket, UUID> {
}
