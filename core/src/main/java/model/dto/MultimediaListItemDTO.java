package model.dto;

import model.entity.MultimediaListItem;
import model.entity.MultimediaStatus;

import java.util.Objects;

public class MultimediaListItemDTO {

    private Long listId;
    private MultimediaStatus status;
    private Integer currentEpisode;
    private MultimediaSummaryDTO multimedia;

    public MultimediaListItemDTO() {}

    public MultimediaListItemDTO(MultimediaListItem multimediaListItem) {
        status = multimediaListItem.getStatus();
        currentEpisode = multimediaListItem.getCurrentEpisode();
        listId = multimediaListItem.getList().getId();
        multimedia = MultimediaSummaryDTO.fromEntity(multimediaListItem.getMultimedia());
    }

    public MultimediaListItemDTO(Long listId, MultimediaStatus status, Integer currentEpisode, MultimediaSummaryDTO multimedia) {
        this.listId = listId;
        this.status = status;
        this.currentEpisode = currentEpisode;
        this.multimedia = multimedia;
    }

    public MultimediaStatus getStatus() {
        return status;
    }

    public void setStatus(MultimediaStatus status) {
        this.status = status;
    }

    public Integer getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(Integer currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public MultimediaSummaryDTO getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(MultimediaSummaryDTO multimedia) {
        this.multimedia = multimedia;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MultimediaListItemDTO that)) return false;
        return Objects.equals(listId, that.listId) && Objects.equals(multimedia, that.multimedia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId, multimedia);
    }
}
