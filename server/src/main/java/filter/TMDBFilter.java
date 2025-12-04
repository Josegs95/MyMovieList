package filter;

import dto.MultimediaSummaryDTO;
import entity.MultimediaType;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class TMDBFilter {

    private static final Integer MIN_VOTE_COUNT = 20;
    private static final Double EXACT_MATCH_SCORE = 1000.0;
    private static final Double STARTS_WITH_SCORE = 15.0;
    private static final Double CONTAINS_SCORE = 5.0;

    public static List<MultimediaSummaryDTO> sortResults(List<MultimediaSummaryDTO> list, String query) {
        String normalizedQuery = normalize(query);

        list = list.stream()
                .filter(dto -> dto.getType() != MultimediaType.PERSON)
                .filter(dto -> dto.getPosterPath() != null)
                .filter(dto -> dto.getVoteCount() > MIN_VOTE_COUNT)
                .sorted(Comparator.comparing((MultimediaSummaryDTO a) -> calculateScore(a, normalizedQuery)).reversed())
                .toList();

        return list;
    }

    private static Double calculateScore(MultimediaSummaryDTO item, String query) {
        Double score = item.getPopularity();

        if (item.getTitle() == null) {
            return 0.0;
        }

        String normalizedTitle = normalize(item.getTitle());
        if (normalizedTitle.equals(query)) {
            score += EXACT_MATCH_SCORE;
        }
        else if (normalizedTitle.startsWith(query)) {
            score += STARTS_WITH_SCORE;
        }
        else if (normalizedTitle.contains(query)) {
            score += CONTAINS_SCORE;
        }

        return score;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().trim();
    }
}
