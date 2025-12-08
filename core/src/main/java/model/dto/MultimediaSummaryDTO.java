package model.dto;

import com.fasterxml.jackson.annotation.*;
import model.entity.Multimedia;
import model.entity.MultimediaType;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "media_type",
        visible = true,
        defaultImpl = MultimediaSummaryDTO.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovieSummaryDTO.class, name = "movie"),
        @JsonSubTypes.Type(value = SeriesSummaryDTO.class, name = "tv")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultimediaSummaryDTO {

    @JsonAlias({"name", "title"})
    private String title;

    @JsonProperty("id")
    private String apiId;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("media_type")
    private MultimediaType type;

    @JsonProperty("vote_average")
    private Double score;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty
    private Double popularity;

    private Long idDb;

    public MultimediaSummaryDTO(){}

    protected MultimediaSummaryDTO(Multimedia multimedia) {
        title = multimedia.getTitle();
        apiId = multimedia.getApiId();
        posterPath = multimedia.getPosterPath();
        type = multimedia.getMultimediaType();
        idDb = multimedia.getId();
    }

    public static MultimediaSummaryDTO fromEntity(Multimedia multimedia) {
        if (multimedia.getMultimediaType() == MultimediaType.MOVIE) {
            return new MovieSummaryDTO(multimedia);
        } else if (multimedia.getMultimediaType() == MultimediaType.SERIES) {
            return new SeriesSummaryDTO(multimedia);
        }

        return new MultimediaSummaryDTO(multimedia);
    }

    public String getTitle() {
        return title;
    }

    public String getApiId() {
        return apiId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public MultimediaType getType() {
        return type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setType(MultimediaType type) {
        this.type = type;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Long getIdDb() {
        return idDb;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MultimediaSummaryDTO dto)) return false;
        return Objects.equals(idDb, dto.idDb)
                || (Objects.equals(apiId, dto.apiId) && type == dto.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiId, type);
    }
}
