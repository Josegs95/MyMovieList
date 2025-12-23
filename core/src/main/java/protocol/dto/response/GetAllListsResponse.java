package protocol.dto.response;

import model.dto.UserListDTO;

import java.util.List;

public record GetAllListsResponse(List<UserListDTO> lists) {
}
