package ui.event;

import model.dto.UserDTO;

public record LoginUserEvent(UserDTO user) implements Event {
}
