package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PartnerInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;


    private Date interviewDate;

    private Double globalScore;

    @Enumerated(EnumType.STRING)
    private InterviewDecision decision;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL)
    private InterviewMeeting meeting;
}
