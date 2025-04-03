package model;

import java.time.LocalDate;
import java.util.List;

public class TvShow implements Multimedia {
    private final int id;
    private String title;
    private String posterUrl;
    private LocalDate releaseDate;
    private String score;
    private Double popularity;

    private String synopsis;
    private List<String> genreList;
    private String country;

    private String status;
    private int totalEpisodes;
    private int totalSeasons;
    private String episodeDuration;

    public TvShow(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getScore() {
        return score;
    }

    public Double getPopularity() {
        return popularity;
    }

    @Override
    public String getSynopsis() {
        return synopsis;
    }

    @Override
    public List<String> getGenreList() {
        return genreList;
    }

    public String getStatus() {
        return status;
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

    public void setStatus(String status) {
        this.status = status;
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
    public String getCountry() {
        return country;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
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
}
