package protocol.dto.response;

import model.dto.UserDTO;

public record LoginResponse(UserDTO userDTO) {
}
