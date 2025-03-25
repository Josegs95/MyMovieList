package model;

import java.time.LocalDate;
import java.util.List;

public class TVShow implements Multimedia {
    final private int ID;
    private String title;
    private String posterURL;
    private LocalDate releaseDate;
    private String score;
    private Double popularity;

    private String synopsis;
    private List<String> genreList;
    private String country;

    private String status;
    private int episodeCount;
    private int seasonCount;
    private String episodeDuration;

    public TVShow(int id) {
        this.ID = id;
    }

    @Override
    public int getId() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterURL() {
        return posterURL;
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

    public int getEpisodeCount() {
        return episodeCount;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public String getEpisodeDuration() {
        if (episodeDuration == null)
            return "Unknown";
        return episodeDuration;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
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

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
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
                ", posterURL='" + posterURL + '\'' +
                ", releaseDate=" + releaseDate +
                ", score='" + score + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
