package org.example.campconnect.dto;


import java.util.Date;
import java.sql.Time;
import java.util.List;

public class PartnershipDTOs {

    /** Site pour l’écran « Campings » de l’admin partenariat. */
    public static class CampingPartnershipDTO {
        private Long campingId;
        private String name;
        private String localisation;
        private int capacite;
        private List<Long> partnerIds;

        public CampingPartnershipDTO() {}

        public Long getCampingId() { return campingId; }
        public void setCampingId(Long campingId) { this.campingId = campingId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocalisation() { return localisation; }
        public void setLocalisation(String localisation) { this.localisation = localisation; }
        public int getCapacite() { return capacite; }
        public void setCapacite(int capacite) { this.capacite = capacite; }
        public List<Long> getPartnerIds() { return partnerIds; }
        public void setPartnerIds(List<Long> partnerIds) { this.partnerIds = partnerIds; }
    }

    public static class ContratDTO {
        private Long contractId;
        private Date startDate;
        private Date endDate;
        private Double commission;
        private String status;
        private Long offerId;

        public ContratDTO() {}
        public ContratDTO(Long contractId, Date startDate, Date endDate, Double commission, String status, Long offerId) {
            this.contractId = contractId; this.startDate = startDate; this.endDate = endDate;
            this.commission = commission; this.status = status; this.offerId = offerId;
        }
        public Long getContractId() { return contractId; }
        public void setContractId(Long contractId) { this.contractId = contractId; }
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
        public Double getCommission() { return commission; }
        public void setCommission(Double commission) { this.commission = commission; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getOfferId() { return offerId; }
        public void setOfferId(Long offerId) { this.offerId = offerId; }
    }

    public static class OfferDTO {
        private Long offerId;
        private String title;
        private String description;
        private Date startDate;
        private Date endDate;
        private Double price;
        private String status;
        private Long campingId;

        public OfferDTO() {}
        public OfferDTO(Long offerId, String title, String description, Date startDate, Date endDate, Double price, String status, Long campingId) {
            this.offerId = offerId; this.title = title; this.description = description;
            this.startDate = startDate; this.endDate = endDate; this.price = price; this.status = status;
            this.campingId = campingId;
        }
        public Long getOfferId() { return offerId; }
        public void setOfferId(Long offerId) { this.offerId = offerId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getCampingId() { return campingId; }
        public void setCampingId(Long campingId) { this.campingId = campingId; }
    }

    public static class PartnerInterviewDTO {
        private Long interviewId;
        private Date interviewDate;
        private Double globalScore;
        private String decision;
        private Long userId;

        public PartnerInterviewDTO() {}
        public PartnerInterviewDTO(Long interviewId, Date interviewDate, Double globalScore, String decision, Long userId) {
            this.interviewId = interviewId; this.interviewDate = interviewDate;
            this.globalScore = globalScore; this.decision = decision; this.userId = userId;
        }
        public Long getInterviewId() { return interviewId; }
        public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
        public Date getInterviewDate() { return interviewDate; }
        public void setInterviewDate(Date interviewDate) { this.interviewDate = interviewDate; }
        public Double getGlobalScore() { return globalScore; }
        public void setGlobalScore(Double globalScore) { this.globalScore = globalScore; }
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    public static class InterviewMeetingDTO {
        private Long meetingId;
        private Date meetingDate;
        private Time startTime;
        private Time endTime;
        private String mode;
        private String location;
        private String report;
        private Long interviewId;

        public InterviewMeetingDTO() {}
        public InterviewMeetingDTO(Long meetingId, Date meetingDate, Time startTime, Time endTime, String mode, String location, String report, Long interviewId) {
            this.meetingId = meetingId; this.meetingDate = meetingDate; this.startTime = startTime;
            this.endTime = endTime; this.mode = mode; this.location = location;
            this.report = report; this.interviewId = interviewId;
        }
        public Long getMeetingId() { return meetingId; }
        public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
        public Date getMeetingDate() { return meetingDate; }
        public void setMeetingDate(Date meetingDate) { this.meetingDate = meetingDate; }
        public Time getStartTime() { return startTime; }
        public void setStartTime(Time startTime) { this.startTime = startTime; }
        public Time getEndTime() { return endTime; }
        public void setEndTime(Time endTime) { this.endTime = endTime; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getReport() { return report; }
        public void setReport(String report) { this.report = report; }
        public Long getInterviewId() { return interviewId; }
        public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
    }

    public static class PartnerQuizDTO {
        private Long quizId;
        private String title;
        private Double maxScore;
        private Long userId;

        public PartnerQuizDTO() {}
        public PartnerQuizDTO(Long quizId, String title, Double maxScore, Long userId) {
            this.quizId = quizId; this.title = title; this.maxScore = maxScore; this.userId = userId;
        }
        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Double getMaxScore() { return maxScore; }
        public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    public static class PartnerQuestionDTO {
        private Long questionId;
        private String label;
        private String type;
        private Double weight;
        private Long quizId;

        public PartnerQuestionDTO() {}
        public PartnerQuestionDTO(Long questionId, String label, String type, Double weight, Long quizId) {
            this.questionId = questionId; this.label = label; this.type = type;
            this.weight = weight; this.quizId = quizId;
        }
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }
    }

    public static class QuizReponsesDTO {
        private Long responseId;
        private String value;
        private Double grade;
        private Long questionId;

        public QuizReponsesDTO() {}
        public QuizReponsesDTO(Long responseId, String value, Double grade, Long questionId) {
            this.responseId = responseId; this.value = value;
            this.grade = grade; this.questionId = questionId;
        }
        public Long getResponseId() { return responseId; }
        public void setResponseId(Long responseId) { this.responseId = responseId; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public Double getGrade() { return grade; }
        public void setGrade(Double grade) { this.grade = grade; }
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
    }

    /** Création / mise à jour partenaire (API). Le score est calculé côté serveur (quiz). */
    public static class PartnerUserWriteDTO {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        /** Optionnel : si vide à la création, mot de passe par défaut côté serveur. */
        private String password;
        private boolean actif;

        public PartnerUserWriteDTO() {}

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public boolean isActif() { return actif; }
        public void setActif(boolean actif) { this.actif = actif; }
    }

    /** Résumé partenaire pour l’UI Angular (liste déroulante, filtres). */
    public static class PartnerUserSummaryDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private double score;
        private boolean actif;

        public PartnerUserSummaryDTO() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public boolean isActif() { return actif; }
        public void setActif(boolean actif) { this.actif = actif; }
    }
}