package model;

import java.time.LocalDate;
import java.util.List;

public interface Multimedia {

    String getTitle();

    String getPosterUrl();

    LocalDate getReleaseDate();

    String getScore();

    Double getPopularity();

    int getId();

    String getSynopsis();

    List<String> getGenreList();

    String getCountry();

    MultimediaType getMultimediaType();

    void setTitle(String title);

    void setPosterUrl(String posterUrl);

    void setReleaseDate(LocalDate releaseDate);

    void setScore(String score);

    void setPopularity(Double popularity);

    void setSynopsis(String synopsis);

    void setGenreList(List<String> genreList);

    void setCountry(String country);
}
