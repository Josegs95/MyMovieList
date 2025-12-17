package model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class MovieDetailDTO extends MultimediaDetailDTO {

    @JsonProperty("runtime")
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
