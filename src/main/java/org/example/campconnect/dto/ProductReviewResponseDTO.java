package org.example.campconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponseDTO {
    private Long idProductReview;
    private Integer rating;
    private String comment;
    private Date reviewDate;
    private String reviewerName;
    private Long reviewerId;
}