package ui.event;

import model.dto.UserDTO;

public record UserAuthenticatedEvent(UserDTO user) implements Event{
}
