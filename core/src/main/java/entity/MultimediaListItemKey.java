package entity;

import java.io.Serializable;
import java.util.Objects;

public class MultimediaListItemKey implements Serializable {

    private Long multimedia;
    private Long list;

    public MultimediaListItemKey() {
    }

    public Long getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Long multimedia) {
        this.multimedia = multimedia;
    }

    public Long getList() {
        return list;
    }

    public void setList(Long list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MultimediaListItemKey that)) return false;
        return Objects.equals(multimedia, that.multimedia) && Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multimedia, list);
    }
}
