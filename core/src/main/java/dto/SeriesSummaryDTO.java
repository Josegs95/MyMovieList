package dto;

import entity.Multimedia;

public class SeriesSummaryDTO extends MultimediaSummaryDTO{

    private Integer totalEpisodes;

    public SeriesSummaryDTO(){}

    public SeriesSummaryDTO(Multimedia multimedia) {
        super(multimedia);
        totalEpisodes = multimedia.getTotalEpisodes();
    }

    public Integer getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(Integer totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }
}
