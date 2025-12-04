package ui.event;

import dto.UserDTO;

public record UserAuthenticatedEvent(UserDTO user) implements Event{
}
