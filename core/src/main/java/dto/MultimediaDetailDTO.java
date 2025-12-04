package dto;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type",
        defaultImpl = MultimediaDetailDTO.class,
        requireTypeIdForSubtypes = OptBoolean.FALSE)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovieDetailDTO.class, name = "movie"),
        @JsonSubTypes.Type(value = SeriesDetailDTO.class, name = "tv")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultimediaDetailDTO {

    @JsonAlias({"title", "name"})
    private String title;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonAlias({"release_date", "first_air_date"})
    private LocalDate releaseDate;

    @JsonProperty("vote_average")
    private Double score;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonProperty("overview")
    private String overview;

    private List<String> genreList;

    private String country;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<String> getGenreList() {
        return genreList;
    }

    @JsonProperty("genres")
    public void setGenreList(List<Object> genreList) {
        if (genreList == null || genreList.isEmpty()) {
            this.genreList = new ArrayList<>();
            return;
        }

        this.genreList = genreList.stream()
                .map(genre -> {
                    if (genre instanceof Map<?,?> map) {
                        return map.get("name").toString();
                    } else {
                        return genre.toString();
                    }
                })
                .toList();
    }

    public String getCountry() {
        return country;
    }

    @JsonProperty("production_countries")
    public void setCountry(Object object) {
        if (object instanceof List<?> list) {
            Map<?, ?> country = (Map<?, ?>) list.getFirst();
            this.country = country.get("iso_3166_1").toString();
        } else {
            this.country = object.toString();
        }
    }

    @Override
    public String toString() {
        return "MultimediaDetailDTO {" +
                "title = \"" + title + '\"' +
                ", posterPath = \"" + posterPath + '\"' +
                ", releaseDate = " + releaseDate +
                ", score = " + score +
                ", popularity = " + popularity +
                ", overview = \"" + overview + '\"' +
                ", genreList = " + genreList +
                ", country = \"" + country + '\"' +
                '}';
    }
}
