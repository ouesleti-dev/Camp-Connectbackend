package projectexamen.spring.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.CampingPartnershipDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.PartnerUserSummaryDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.PartnerUserWriteDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.ContratDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.OfferDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.PartnerInterviewDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.InterviewMeetingDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.PartnerQuizDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.PartnerQuestionDTO;
import projectexamen.spring.campconnect.DTO.PartnershipDTOs.QuizReponsesDTO;
import projectexamen.spring.campconnect.Services.IPatnershipService;

import java.util.List;

@RestController
@RequestMapping("/api/partnership")
@RequiredArgsConstructor
public class PatnershipController {

    private final IPatnershipService service;

    @GetMapping("/partner-users")
    public ResponseEntity<List<PartnerUserSummaryDTO>> getPartnerUsers() {
        return ResponseEntity.ok(service.getPartnerUsers());
    }

    @PostMapping("/partner-users")
    public ResponseEntity<PartnerUserSummaryDTO> createPartnerUser(@RequestBody PartnerUserWriteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPartnerUser(dto));
    }

    @PutMapping("/partner-users/{id}")
    public ResponseEntity<PartnerUserSummaryDTO> updatePartnerUser(
            @PathVariable Long id, @RequestBody PartnerUserWriteDTO dto) {
        return ResponseEntity.ok(service.updatePartnerUser(id, dto));
    }

    @DeleteMapping("/partner-users/{id}")
    public ResponseEntity<Void> deletePartnerUser(@PathVariable Long id) {
        service.deletePartnerUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/campings")
    public ResponseEntity<List<CampingPartnershipDTO>> getPartnershipCampings() {
        return ResponseEntity.ok(service.getPartnershipCampings());
    }

    @PostMapping("/campings")
    public ResponseEntity<CampingPartnershipDTO> createCamping(@RequestBody CampingPartnershipDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCamping(dto));
    }

    @PutMapping("/campings/{id}")
    public ResponseEntity<CampingPartnershipDTO> updateCamping(
            @PathVariable Long id, @RequestBody CampingPartnershipDTO dto) {
        return ResponseEntity.ok(service.updateCamping(id, dto));
    }

    @DeleteMapping("/campings/{id}")
    public ResponseEntity<Void> deleteCamping(@PathVariable Long id) {
        service.deleteCamping(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  CONTRAT  →  /api/partnership/contrats
    // ════════════════════════════════════════════════════════

    @GetMapping("/contrats")
    public ResponseEntity<List<ContratDTO>> getAllContrats() {
        return ResponseEntity.ok(service.getAllContrats());
    }

    @GetMapping("/contrats/{id}")
    public ResponseEntity<ContratDTO> getContratById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getContratById(id));
    }

    @PostMapping("/contrats")
    public ResponseEntity<ContratDTO> createContrat(@RequestBody ContratDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createContrat(dto));
    }

    @PutMapping("/contrats/{id}")
    public ResponseEntity<ContratDTO> updateContrat(@PathVariable Long id, @RequestBody ContratDTO dto) {
        return ResponseEntity.ok(service.updateContrat(id, dto));
    }

    @DeleteMapping("/contrats/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        service.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  OFFER  →  /api/partnership/offers
    // ════════════════════════════════════════════════════════

    @GetMapping("/offers")
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        return ResponseEntity.ok(service.getAllOffers());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOfferById(id));
    }

    @PostMapping("/offers")
    public ResponseEntity<OfferDTO> createOffer(@RequestBody OfferDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOffer(dto));
    }

    @PutMapping("/offers/{id}")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable Long id, @RequestBody OfferDTO dto) {
        return ResponseEntity.ok(service.updateOffer(id, dto));
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        service.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  PARTNER INTERVIEW  →  /api/partnership/interviews
    // ════════════════════════════════════════════════════════

    @GetMapping("/interviews")
    public ResponseEntity<List<PartnerInterviewDTO>> getAllInterviews() {
        return ResponseEntity.ok(service.getAllInterviews());
    }

    @GetMapping("/interviews/{id}")
    public ResponseEntity<PartnerInterviewDTO> getInterviewById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInterviewById(id));
    }

    @PostMapping("/interviews")
    public ResponseEntity<PartnerInterviewDTO> createInterview(@RequestBody PartnerInterviewDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createInterview(dto));
    }

    @PutMapping("/interviews/{id}")
    public ResponseEntity<PartnerInterviewDTO> updateInterview(@PathVariable Long id, @RequestBody PartnerInterviewDTO dto) {
        return ResponseEntity.ok(service.updateInterview(id, dto));
    }

    @DeleteMapping("/interviews/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long id) {
        service.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  INTERVIEW MEETING  →  /api/partnership/meetings
    // ════════════════════════════════════════════════════════

    @GetMapping("/meetings")
    public ResponseEntity<List<InterviewMeetingDTO>> getAllMeetings() {
        return ResponseEntity.ok(service.getAllMeetings());
    }

    @GetMapping("/meetings/{id}")
    public ResponseEntity<InterviewMeetingDTO> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMeetingById(id));
    }

    @PostMapping("/meetings")
    public ResponseEntity<InterviewMeetingDTO> createMeeting(@RequestBody InterviewMeetingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMeeting(dto));
    }

    @PutMapping("/meetings/{id}")
    public ResponseEntity<InterviewMeetingDTO> updateMeeting(@PathVariable Long id, @RequestBody InterviewMeetingDTO dto) {
        return ResponseEntity.ok(service.updateMeeting(id, dto));
    }

    @DeleteMapping("/meetings/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        service.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  PARTNER QUIZ  →  /api/partnership/quizzes
    // ════════════════════════════════════════════════════════

    @GetMapping("/quizzes")
    public ResponseEntity<List<PartnerQuizDTO>> getAllQuizzes() {
        return ResponseEntity.ok(service.getAllQuizzes());
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<PartnerQuizDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getQuizById(id));
    }

    @PostMapping("/quizzes")
    public ResponseEntity<PartnerQuizDTO> createQuiz(@RequestBody PartnerQuizDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQuiz(dto));
    }

    @PutMapping("/quizzes/{id}")
    public ResponseEntity<PartnerQuizDTO> updateQuiz(@PathVariable Long id, @RequestBody PartnerQuizDTO dto) {
        return ResponseEntity.ok(service.updateQuiz(id, dto));
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        service.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  PARTNER QUESTION  →  /api/partnership/questions
    // ════════════════════════════════════════════════════════

    @GetMapping("/questions")
    public ResponseEntity<List<PartnerQuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<PartnerQuestionDTO> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getQuestionById(id));
    }

    @PostMapping("/questions")
    public ResponseEntity<PartnerQuestionDTO> createQuestion(@RequestBody PartnerQuestionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQuestion(dto));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<PartnerQuestionDTO> updateQuestion(@PathVariable Long id, @RequestBody PartnerQuestionDTO dto) {
        return ResponseEntity.ok(service.updateQuestion(id, dto));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        service.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════
    //  QUIZ REPONSES  →  /api/partnership/reponses
    // ════════════════════════════════════════════════════════

    @GetMapping("/reponses")
    public ResponseEntity<List<QuizReponsesDTO>> getAllReponses() {
        return ResponseEntity.ok(service.getAllReponses());
    }

    @GetMapping("/reponses/{id}")
    public ResponseEntity<QuizReponsesDTO> getReponseById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getReponseById(id));
    }

    @PostMapping("/reponses")
    public ResponseEntity<QuizReponsesDTO> createReponse(@RequestBody QuizReponsesDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReponse(dto));
    }

    @PutMapping("/reponses/{id}")
    public ResponseEntity<QuizReponsesDTO> updateReponse(@PathVariable Long id, @RequestBody QuizReponsesDTO dto) {
        return ResponseEntity.ok(service.updateReponse(id, dto));
    }

    @DeleteMapping("/reponses/{id}")
    public ResponseEntity<Void> deleteReponse(@PathVariable Long id) {
        service.deleteReponse(id);
        return ResponseEntity.noContent().build();
    }
}