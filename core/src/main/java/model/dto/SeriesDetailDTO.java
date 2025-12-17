package model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("unused")
public class SeriesDetailDTO extends MultimediaDetailDTO {

    @JsonProperty("status")
    private String airingStatus;

    @JsonProperty("number_of_episodes")
    private Integer totalEpisodes;

    @JsonProperty("number_of_seasons")
    private Integer totalSeasons;

    private String episodeDuration;

    public String getAiringStatus() {
        return airingStatus;
    }

    public void setAiringStatus(String airingStatus) {
        this.airingStatus = airingStatus;
    }

    public Integer getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(Integer totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }

    public void setTotalSeasons(Integer totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public String getEpisodeDuration() {
        return episodeDuration;
    }

    @JsonProperty("episode_run_time")
    public void setEpisodeDuration(Object object) {
        if (object instanceof List<?> list) {
            int durationSum = list.stream()
                    .map(duration -> (Integer) duration)
                    .reduce(Math::addExact).orElse(0);
            int averageDuration = (int) (durationSum / (double) list.size());
            this.episodeDuration = String.valueOf(averageDuration);
        } else {
            this.episodeDuration = object.toString();
        }
    }

}
