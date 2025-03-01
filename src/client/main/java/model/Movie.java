package model;

import java.time.LocalDate;
import java.util.List;

public class Movie implements Multimedia {
    private int id;
    private String title;
    private String posterURL;
    private LocalDate releaseDate;
    private String score;
    private Double popularity;

    private String synopsis;
    private List<String> genreList;
    private String duration;
    private String country;

    public Movie() {
    }

    public int getId() {
        return id;
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

    public String getDuration() {
        return duration;
    }

    @Override
    public void setId(int id) {
        this.id = id;
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

    public void setDuration(String duration) {
        int durationAsInt = Integer.parseInt(duration);
        int hours = durationAsInt / 60;
        int minutes = durationAsInt % 60;

        if (hours == 0)
            this.duration = minutes + " minutes";
        else
            this.duration = hours + "h " + minutes + " minutes";
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
        return "Movie{" +
                "title='" + title + '\'' +
                ", posterURL='" + posterURL + '\'' +
                ", releaseDate=" + releaseDate +
                ", score='" + score + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
