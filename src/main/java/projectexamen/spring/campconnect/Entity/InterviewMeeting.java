package projectexamen.spring.campconnect.Entity;

import jakarta.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
public class InterviewMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @Temporal(TemporalType.DATE)
    private Date meetingDate;

    private Time startTime;
    private Time endTime;

    @Enumerated(EnumType.STRING)
    private InterviewMode mode;

    private String location;
    private String report;

    @OneToOne
    @JoinColumn(name = "interview_id")
    private PartnerInterview interview;

    public InterviewMeeting() {}

    public InterviewMeeting(Long meetingId, Date meetingDate, Time startTime, Time endTime, InterviewMode mode, String location, String report, PartnerInterview interview) {
        this.meetingId = meetingId; this.meetingDate = meetingDate;
        this.startTime = startTime; this.endTime = endTime;
        this.mode = mode; this.location = location;
        this.report = report; this.interview = interview;
    }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public Date getMeetingDate() { return meetingDate; }
    public void setMeetingDate(Date meetingDate) { this.meetingDate = meetingDate; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public InterviewMode getMode() { return mode; }
    public void setMode(InterviewMode mode) { this.mode = mode; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getReport() { return report; }
    public void setReport(String report) { this.report = report; }
    public PartnerInterview getInterview() { return interview; }
    public void setInterview(PartnerInterview interview) { this.interview = interview; }
}