package projectexamen.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

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

}