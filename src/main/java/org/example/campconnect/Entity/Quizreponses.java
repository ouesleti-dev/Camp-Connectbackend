package org.example.campconnect.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Quizreponses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;

    private String value;
    private Double grade;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private PartnerQuestion question;

    public Quizreponses() {}

    public Quizreponses(Long responseId, String value, Double grade, PartnerQuestion question) {
        this.responseId = responseId; this.value = value;
        this.grade = grade; this.question = question;
    }

    public Long getResponseId() { return responseId; }
    public void setResponseId(Long responseId) { this.responseId = responseId; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }
    public PartnerQuestion getQuestion() { return question; }
    public void setQuestion(PartnerQuestion question) { this.question = question; }
}