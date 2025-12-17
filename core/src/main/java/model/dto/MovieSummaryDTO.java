package model.dto;

import model.entity.Multimedia;

@SuppressWarnings("unused")
public class MovieSummaryDTO extends MultimediaSummaryDTO{

    public MovieSummaryDTO() {}

    public MovieSummaryDTO(Multimedia multimedia) {
        super(multimedia);
    }
}
