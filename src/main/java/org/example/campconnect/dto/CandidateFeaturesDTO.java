package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CandidateFeaturesDTO {
    @JsonProperty("response_length")
    private Double responseLength;

    @JsonProperty("filler_word_count")
    private Double fillerWordCount;

    @JsonProperty("keyword_match_score")
    private Double keywordMatchScore;

    @JsonProperty("response_time_seconds")
    private Double responseTimeSeconds;

    @JsonProperty("sentiment_score")
    private Double sentimentScore;

    @JsonProperty("coherence_score")
    private Double coherenceScore;

    @JsonProperty("confidence_score")
    private Double confidenceScore;
}
