package projectexamen.spring.campconnect.Entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class PartnerInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;

    @Temporal(TemporalType.DATE)
    private Date interviewDate;

    private Double globalScore;

    @Enumerated(EnumType.STRING)
    private InterviewDecision decision;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL)
    private InterviewMeeting meeting;

    public PartnerInterview() {}

    public PartnerInterview(Long interviewId, Date interviewDate, Double globalScore, InterviewDecision decision, User user, InterviewMeeting meeting) {
        this.interviewId = interviewId; this.interviewDate = interviewDate;
        this.globalScore = globalScore; this.decision = decision;
        this.user = user; this.meeting = meeting;
    }

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
    public Date getInterviewDate() { return interviewDate; }
    public void setInterviewDate(Date interviewDate) { this.interviewDate = interviewDate; }
    public Double getGlobalScore() { return globalScore; }
    public void setGlobalScore(Double globalScore) { this.globalScore = globalScore; }
    public InterviewDecision getDecision() { return decision; }
    public void setDecision(InterviewDecision decision) { this.decision = decision; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public InterviewMeeting getMeeting() { return meeting; }
    public void setMeeting(InterviewMeeting meeting) { this.meeting = meeting; }
}