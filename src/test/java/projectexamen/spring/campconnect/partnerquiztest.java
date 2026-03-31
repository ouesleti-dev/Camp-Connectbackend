package projectexamen.spring.campconnect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import projectexamen.spring.campconnect.Entity.PartnerQuestion;
import projectexamen.spring.campconnect.Entity.PartnerQuiz;
import projectexamen.spring.campconnect.Entity.Role;
import projectexamen.spring.campconnect.Entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests unitaires — Entité PartnerQuiz")
class PartnerQuizTest {

    private PartnerQuiz quiz;
    private User sampleUser;

    // ── helper ────────────────────────────────────────────────────────────

    private User buildUser(Long id, String email, Role role) {
        return User.builder()
                .idUser(id)
                .firstName("Bob").lastName("Martin")
                .email(email).password("encoded")
                .phone("0600000000")
                .role(role).enabled(true)
                .build();
    }

    private PartnerQuestion buildQuestion(Long id, String label, Double weight) {
        PartnerQuestion q = new PartnerQuestion();
        q.setQuestionId(id);
        q.setLabel(label);
        q.setWeight(weight);
        return q;
    }

    @BeforeEach
    void setUp() {
        sampleUser = buildUser(1L, "partner@x.com", Role.PARTNER);

        quiz = new PartnerQuiz();
        quiz.setQuizId(1L);
        quiz.setTitle("Quiz Partenariat 2025");
        quiz.setMaxScore(100.0);
        quiz.setUser(sampleUser);
        quiz.setQuestions(new ArrayList<>());
    }

    // ════════════════════════════════════════════════════════════════════
    //  Constructeurs
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Constructeurs")
    class ConstructorTests {

        @Test
        @DisplayName("NoArgsConstructor — crée une instance avec tous les champs null")
        void noArgsConstructor_allFieldsNull() {
            PartnerQuiz q = new PartnerQuiz();
            assertThat(q.getQuizId()).isNull();
            assertThat(q.getTitle()).isNull();
            assertThat(q.getMaxScore()).isNull();
            assertThat(q.getUser()).isNull();
            assertThat(q.getQuestions()).isNull();
        }

        @Test
        @DisplayName("AllArgsConstructor — initialise tous les champs")
        void allArgsConstructor_initializesAllFields() {
            List<PartnerQuestion> questions = List.of(buildQuestion(1L, "Q1", 2.0));
            PartnerQuiz q = new PartnerQuiz(5L, "Quiz Test", 50.0, sampleUser, questions);

            assertThat(q.getQuizId()).isEqualTo(5L);
            assertThat(q.getTitle()).isEqualTo("Quiz Test");
            assertThat(q.getMaxScore()).isEqualTo(50.0);
            assertThat(q.getUser()).isEqualTo(sampleUser);
            assertThat(q.getQuestions()).hasSize(1);
        }

        @Test
        @DisplayName("AllArgsConstructor — accepte user null et questions null")
        void allArgsConstructor_nullUserAndQuestions() {
            PartnerQuiz q = new PartnerQuiz(1L, "Quiz", 100.0, null, null);
            assertThat(q.getUser()).isNull();
            assertThat(q.getQuestions()).isNull();
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Getters / Setters
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Getters & Setters")
    class GetterSetterTests {

        @Test
        @DisplayName("setQuizId / getQuizId")
        void quizId_getterSetter() {
            quiz.setQuizId(99L);
            assertThat(quiz.getQuizId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("setTitle / getTitle")
        void title_getterSetter() {
            quiz.setTitle("Nouveau Quiz");
            assertThat(quiz.getTitle()).isEqualTo("Nouveau Quiz");
        }

        @Test
        @DisplayName("setTitle — accepte null")
        void title_null() {
            quiz.setTitle(null);
            assertThat(quiz.getTitle()).isNull();
        }

        @Test
        @DisplayName("setTitle — accepte chaîne vide")
        void title_empty() {
            quiz.setTitle("");
            assertThat(quiz.getTitle()).isEmpty();
        }

        @Test
        @DisplayName("setMaxScore / getMaxScore")
        void maxScore_getterSetter() {
            quiz.setMaxScore(200.0);
            assertThat(quiz.getMaxScore()).isEqualTo(200.0);
        }

        @Test
        @DisplayName("setMaxScore — accepte null")
        void maxScore_null() {
            quiz.setMaxScore(null);
            assertThat(quiz.getMaxScore()).isNull();
        }

        @Test
        @DisplayName("setMaxScore — accepte zéro")
        void maxScore_zero() {
            quiz.setMaxScore(0.0);
            assertThat(quiz.getMaxScore()).isZero();
        }

        @Test
        @DisplayName("setUser / getUser — lie un PARTNER")
        void user_getterSetter() {
            User newUser = buildUser(2L, "new@x.com", Role.PARTNER);
            quiz.setUser(newUser);
            assertThat(quiz.getUser().getIdUser()).isEqualTo(2L);
        }

        @Test
        @DisplayName("setUser — accepte null")
        void user_null() {
            quiz.setUser(null);
            assertThat(quiz.getUser()).isNull();
        }

        @Test
        @DisplayName("setQuestions / getQuestions — liste de questions")
        void questions_getterSetter() {
            List<PartnerQuestion> qs = List.of(
                    buildQuestion(1L, "Q1", 1.0),
                    buildQuestion(2L, "Q2", 2.0)
            );
            quiz.setQuestions(qs);
            assertThat(quiz.getQuestions()).hasSize(2)
                    .extracting(PartnerQuestion::getLabel)
                    .containsExactly("Q1", "Q2");
        }

        @Test
        @DisplayName("setQuestions — accepte liste vide")
        void questions_empty() {
            quiz.setQuestions(new ArrayList<>());
            assertThat(quiz.getQuestions()).isEmpty();
        }

        @Test
        @DisplayName("setQuestions — accepte null")
        void questions_null() {
            quiz.setQuestions(null);
            assertThat(quiz.getQuestions()).isNull();
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Cohérence métier
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Cohérence métier")
    class BusinessLogicTests {

        @Test
        @DisplayName("maxScore positive — configuration valide")
        void maxScore_positive() {
            quiz.setMaxScore(100.0);
            assertThat(quiz.getMaxScore()).isPositive();
        }

        @Test
        @DisplayName("somme des poids des questions inférieure au maxScore")
        void questions_totalWeight_lessThanMaxScore() {
            List<PartnerQuestion> qs = List.of(
                    buildQuestion(1L, "Q1", 30.0),
                    buildQuestion(2L, "Q2", 40.0),
                    buildQuestion(3L, "Q3", 30.0)
            );
            quiz.setMaxScore(100.0);
            quiz.setQuestions(qs);

            double totalWeight = quiz.getQuestions().stream()
                    .mapToDouble(PartnerQuestion::getWeight)
                    .sum();

            assertThat(totalWeight).isLessThanOrEqualTo(quiz.getMaxScore());
        }

        @Test
        @DisplayName("remplacement du user — met à jour la référence")
        void user_replacement_updatesReference() {
            User newUser = buildUser(99L, "other@x.com", Role.PARTNER);
            quiz.setUser(newUser);
            assertThat(quiz.getUser().getIdUser()).isEqualTo(99L);
        }

        @Test
        @DisplayName("modification du titre ne change pas le maxScore")
        void title_change_doesNotAffectMaxScore() {
            Double originalScore = quiz.getMaxScore();
            quiz.setTitle("Titre modifié");
            assertThat(quiz.getMaxScore()).isEqualTo(originalScore);
        }

        @Test
        @DisplayName("quiz sans questions — liste vide")
        void quiz_withNoQuestions_isEmpty() {
            quiz.setQuestions(new ArrayList<>());
            assertThat(quiz.getQuestions()).isEmpty();
        }

        @Test
        @DisplayName("deux quiz distincts sont indépendants")
        void twoQuizzes_areIndependent() {
            PartnerQuiz q1 = new PartnerQuiz();
            q1.setTitle("Quiz A"); q1.setMaxScore(50.0);

            PartnerQuiz q2 = new PartnerQuiz();
            q2.setTitle("Quiz B"); q2.setMaxScore(80.0);

            assertThat(q1.getTitle()).isNotEqualTo(q2.getTitle());
            assertThat(q1.getMaxScore()).isNotEqualTo(q2.getMaxScore());
        }

        @Test
        @DisplayName("getQuestions — retourne toutes les questions ajoutées")
        void questions_getAll() {
            List<PartnerQuestion> qs = new ArrayList<>();
            qs.add(buildQuestion(1L, "Q1", 10.0));
            qs.add(buildQuestion(2L, "Q2", 20.0));
            qs.add(buildQuestion(3L, "Q3", 30.0));
            quiz.setQuestions(qs);

            assertThat(quiz.getQuestions()).hasSize(3);
        }
    }
}