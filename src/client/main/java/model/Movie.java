package model;

import java.time.LocalDate;

public class Movie implements Multimedia {
    private String title;
    private String posterURL;
    private LocalDate releaseDate;
    private String score;
    private Double popularity;

    public Movie() {
    }

    public Movie(String title, String posterURL, LocalDate releaseDate, String score, Double popularity) {
        this.title = title;
        this.posterURL = posterURL;
        this.releaseDate = releaseDate;
        this.score = score;
        this.popularity = popularity;
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
