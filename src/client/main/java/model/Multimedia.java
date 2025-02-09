package model;

import java.time.LocalDate;

public interface Multimedia {
    String getTitle();
    String getPosterURL();
    LocalDate getReleaseDate();
    String getScore();
    Double getPopularity();

    void setTitle(String title);
    void setPosterURL(String posterURL);
    void setReleaseDate(LocalDate releaseDate);
    void setScore(String score);
    void setPopularity(Double popularity);
}
