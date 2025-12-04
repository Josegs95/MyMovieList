package dto.response;

import dto.UserListDTO;

import java.util.List;

public record GetAllListsResponse(List<UserListDTO> lists) {
}
