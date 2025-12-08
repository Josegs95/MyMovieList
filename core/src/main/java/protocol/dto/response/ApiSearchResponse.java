package protocol.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import model.dto.MultimediaSummaryDTO;

import java.util.List;

public record ApiSearchResponse(

        Integer page,

        List<MultimediaSummaryDTO> results,

        @JsonProperty("total_pages")
        Integer totalPages,

        @JsonProperty("total_results")
        Integer totalResults
) {}
