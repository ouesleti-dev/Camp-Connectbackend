package projectexamen.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class InterviewMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @Temporal(TemporalType.DATE)
    private Date meetingDate;

    @Temporal(TemporalType.TIME)
    private Time startTime;

    @Temporal(TemporalType.TIME)
    private Time endTime;

    @Enumerated(EnumType.STRING)
    private InterviewMode mode;

    private String location;
    private String report;

    @OneToOne
    @JoinColumn(name = "interview_id")
    private PartnerInterview interview;
}