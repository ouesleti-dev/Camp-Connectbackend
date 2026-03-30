package projectexamen.spring.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "camping_id")
    private campings camping;

    @OneToMany(mappedBy = "user")
    private List<PartnerInterview> interviews;

    @OneToMany(mappedBy = "user")
    private List<PartnerQuiz> quizzes;

    @Builder.Default
    private boolean enabled = true;
}