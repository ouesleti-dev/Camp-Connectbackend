package projectexamen.spring.campconnect.Services;

import projectexamen.spring.campconnect.DTO.*;

import java.util.List;

public interface IPatnershipService {

    List<PartnershipDTOs.PartnerUserSummaryDTO> getPartnerUsers();

    PartnershipDTOs.PartnerUserSummaryDTO createPartnerUser(PartnershipDTOs.PartnerUserWriteDTO dto);

    PartnershipDTOs.PartnerUserSummaryDTO updatePartnerUser(Long id, PartnershipDTOs.PartnerUserWriteDTO dto);

    void deletePartnerUser(Long id);

    List<PartnershipDTOs.CampingPartnershipDTO> getPartnershipCampings();

    PartnershipDTOs.CampingPartnershipDTO createCamping(PartnershipDTOs.CampingPartnershipDTO dto);

    PartnershipDTOs.CampingPartnershipDTO updateCamping(Long id, PartnershipDTOs.CampingPartnershipDTO dto);

    void deleteCamping(Long id);

    // ── Contrat ──────────────────────────────────────────
    List<PartnershipDTOs.ContratDTO> getAllContrats();
    PartnershipDTOs.ContratDTO getContratById(Long id);
    PartnershipDTOs.ContratDTO createContrat(PartnershipDTOs.ContratDTO dto);
    PartnershipDTOs.ContratDTO updateContrat(Long id, PartnershipDTOs.ContratDTO dto);
    void deleteContrat(Long id);

    // ── Offer ─────────────────────────────────────────────
    List<PartnershipDTOs.OfferDTO> getAllOffers();
    PartnershipDTOs.OfferDTO getOfferById(Long id);
    PartnershipDTOs.OfferDTO createOffer(PartnershipDTOs.OfferDTO dto);
    PartnershipDTOs.OfferDTO updateOffer(Long id, PartnershipDTOs.OfferDTO dto);
    void deleteOffer(Long id);

    // ── PartnerInterview ──────────────────────────────────
    List<PartnershipDTOs.PartnerInterviewDTO> getAllInterviews();
    PartnershipDTOs.PartnerInterviewDTO getInterviewById(Long id);
    PartnershipDTOs.PartnerInterviewDTO createInterview(PartnershipDTOs.PartnerInterviewDTO dto);
    PartnershipDTOs.PartnerInterviewDTO updateInterview(Long id, PartnershipDTOs.PartnerInterviewDTO dto);
    void deleteInterview(Long id);

    // ── InterviewMeeting ──────────────────────────────────
    List<PartnershipDTOs.InterviewMeetingDTO> getAllMeetings();
    PartnershipDTOs.InterviewMeetingDTO getMeetingById(Long id);
    PartnershipDTOs.InterviewMeetingDTO createMeeting(PartnershipDTOs.InterviewMeetingDTO dto);
    PartnershipDTOs.InterviewMeetingDTO updateMeeting(Long id, PartnershipDTOs.InterviewMeetingDTO dto);
    void deleteMeeting(Long id);

    // ── PartnerQuiz ───────────────────────────────────────
    List<PartnershipDTOs.PartnerQuizDTO> getAllQuizzes();
    PartnershipDTOs.PartnerQuizDTO getQuizById(Long id);
    PartnershipDTOs.PartnerQuizDTO createQuiz(PartnershipDTOs.PartnerQuizDTO dto);
    PartnershipDTOs.PartnerQuizDTO updateQuiz(Long id, PartnershipDTOs.PartnerQuizDTO dto);
    void deleteQuiz(Long id);

    // ── PartnerQuestion ───────────────────────────────────
    List<PartnershipDTOs.PartnerQuestionDTO> getAllQuestions();
    PartnershipDTOs.PartnerQuestionDTO getQuestionById(Long id);
    PartnershipDTOs.PartnerQuestionDTO createQuestion(PartnershipDTOs.PartnerQuestionDTO dto);
    PartnershipDTOs.PartnerQuestionDTO updateQuestion(Long id, PartnershipDTOs.PartnerQuestionDTO dto);
    void deleteQuestion(Long id);

    // ── QuizReponses ──────────────────────────────────────
    List<PartnershipDTOs.QuizReponsesDTO> getAllReponses();
    PartnershipDTOs.QuizReponsesDTO getReponseById(Long id);
    PartnershipDTOs.QuizReponsesDTO createReponse(PartnershipDTOs.QuizReponsesDTO dto);
    PartnershipDTOs.QuizReponsesDTO updateReponse(Long id, PartnershipDTOs.QuizReponsesDTO dto);
    void deleteReponse(Long id);
}