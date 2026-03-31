package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Builder
public class PartnerQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    private String title;
    private Double maxScore;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "quiz")
    private List<PartnerQuestion> questions;

    public PartnerQuiz() {}

    public PartnerQuiz(Long quizId, String title, Double maxScore, User user, List<PartnerQuestion> questions) {
        this.quizId = quizId; this.title = title; this.maxScore = maxScore;
        this.user = user; this.questions = questions;
    }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<PartnerQuestion> getQuestions() { return questions; }
    public void setQuestions(List<PartnerQuestion> questions) { this.questions = questions; }
}