package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TvShow implements Multimedia {

    private static final MultimediaType TYPE = MultimediaType.TV_SHOW;
    private final int id;

    private String title;
    private String posterUrl;
    private LocalDate releaseDate;
    private String score;
    private Double popularity;
    private String synopsis;
    private List<String> genreList;
    private String country;
    private String airingStatus;
    private int totalEpisodes;
    private int totalSeasons;
    private String episodeDuration;

    public TvShow(int id) {
        this.id = id;
    }

    public TvShow(int id, String title, String posterUrl, LocalDate releaseDate, String score, Double popularity) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.score = score;
        this.popularity = popularity;
    }

    @Override
    public MultimediaType getMultimediaType() {
        return TYPE;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPosterUrl() {
        return posterUrl;
    }

    @Override
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String getScore() {
        return score;
    }

    @Override
    public Double getPopularity() {
        return popularity;
    }

    @Override
    public String getSynopsis() {
        if (synopsis.isEmpty()) {
            return "-no synopsis found-";
        }
        return synopsis;
    }

    @Override
    public List<String> getGenreList() {
        return genreList;
    }

    @Override
    public String getCountry() {
        return country;
    }

    public String getAiringStatus() {
        return airingStatus;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public int getTotalSeasons() {
        return totalSeasons;
    }

    public String getEpisodeDuration() {
        return episodeDuration;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    @Override
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    @Override
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public void setGenreList(List<String> genreList) {
        this.genreList = genreList;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    public void setAiringStatus(String airingStatus) {
        this.airingStatus = airingStatus;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public void setTotalSeasons(int totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public void setEpisodeDuration(String episodeDuration) {
        this.episodeDuration = episodeDuration + " minutes per ep.";
    }

    @Override
    public String toString() {
        return "TVShow{" +
                "title='" + title + '\'' +
                ", posterURL='" + posterUrl + '\'' +
                ", releaseDate=" + releaseDate +
                ", score='" + score + '\'' +
                ", popularity=" + popularity +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TvShow tvShow)) return false;
        return id == tvShow.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, TYPE);
    }
}
