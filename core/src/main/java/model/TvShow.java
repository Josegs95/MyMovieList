package model;

import model.entity.Multimedia;

import java.time.LocalDate;
import java.util.List;

public class TvShow extends Multimedia {

    private String posterUrl;
    private LocalDate releaseDate;
    private String score;
    private Double popularity;
    private String synopsis;
    private List<String> genreList;
    private String country;

    private String airingStatus;
    private Integer totalEpisodes;
    private Integer totalSeasons;
    private String episodeDuration;

    public TvShow() {
    }

    public TvShow(String posterUrl, LocalDate releaseDate, String score, Double popularity) {
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.score = score;
        this.popularity = popularity;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<String> genreList) {
        this.genreList = genreList;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAiringStatus() {
        return airingStatus;
    }

    public void setAiringStatus(String airingStatus) {
        this.airingStatus = airingStatus;
    }

    @Override
    public Integer getTotalEpisodes() {
        return totalEpisodes;
    }

    @Override
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

    public void setEpisodeDuration(String episodeDuration) {
        this.episodeDuration = episodeDuration;
    }
}
