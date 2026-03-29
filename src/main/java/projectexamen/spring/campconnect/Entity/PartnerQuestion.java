package projectexamen.spring.campconnect.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class PartnerQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private String label;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    private Double weight;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private PartnerQuiz quiz;

    @OneToMany(mappedBy = "question")
    private List<Quizreponses> responses;

    public PartnerQuestion() {}

    public PartnerQuestion(Long questionId, String label, QuestionType type, Double weight, PartnerQuiz quiz, List<Quizreponses> responses) {
        this.questionId = questionId; this.label = label; this.type = type;
        this.weight = weight; this.quiz = quiz; this.responses = responses;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public PartnerQuiz getQuiz() { return quiz; }
    public void setQuiz(PartnerQuiz quiz) { this.quiz = quiz; }
    public List<Quizreponses> getResponses() { return responses; }
    public void setResponses(List<Quizreponses> responses) { this.responses = responses; }
}