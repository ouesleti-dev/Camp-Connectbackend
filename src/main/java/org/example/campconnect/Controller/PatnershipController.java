package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IPatnershipService;
import org.example.campconnect.dto.PartnershipDTOs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/partnership")
@RequiredArgsConstructor
public class PatnershipController {

    private final IPatnershipService service;

    @GetMapping("/partner-users")
    public ResponseEntity<List<PartnershipDTOs.PartnerUserSummaryDTO>> getPartnerUsers() {
        return ResponseEntity.ok(service.getPartnerUsers());
    }

    @PostMapping("/partner-users")
    public ResponseEntity<PartnershipDTOs.PartnerUserSummaryDTO> createPartnerUser(@RequestBody PartnershipDTOs.PartnerUserWriteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPartnerUser(dto));
    }

    @PutMapping("/partner-users/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerUserSummaryDTO> updatePartnerUser(
            @PathVariable Long id, @RequestBody PartnershipDTOs.PartnerUserWriteDTO dto) {
        return ResponseEntity.ok(service.updatePartnerUser(id, dto));
    }

    @DeleteMapping("/partner-users/{id}")
    public ResponseEntity<Void> deletePartnerUser(@PathVariable Long id) {
        service.deletePartnerUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/campings")
    public ResponseEntity<List<PartnershipDTOs.CampingPartnershipDTO>> getPartnershipCampings() {
        return ResponseEntity.ok(service.getPartnershipCampings());
    }

    @PostMapping("/campings")
    public ResponseEntity<PartnershipDTOs.CampingPartnershipDTO> createCamping(@RequestBody PartnershipDTOs.CampingPartnershipDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCamping(dto));
    }

    @PutMapping("/campings/{id}")
    public ResponseEntity<PartnershipDTOs.CampingPartnershipDTO> updateCamping(
            @PathVariable Long id, @RequestBody PartnershipDTOs.CampingPartnershipDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.ContratDTO>> getAllContrats() {
        return ResponseEntity.ok(service.getAllContrats());
    }

    @GetMapping("/contrats/{id}")
    public ResponseEntity<PartnershipDTOs.ContratDTO> getContratById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getContratById(id));
    }

    @PostMapping("/contrats")
    public ResponseEntity<PartnershipDTOs.ContratDTO> createContrat(@RequestBody PartnershipDTOs.ContratDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createContrat(dto));
    }

    @PutMapping("/contrats/{id}")
    public ResponseEntity<PartnershipDTOs.ContratDTO> updateContrat(@PathVariable Long id, @RequestBody PartnershipDTOs.ContratDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.OfferDTO>> getAllOffers() {
        return ResponseEntity.ok(service.getAllOffers());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<PartnershipDTOs.OfferDTO> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOfferById(id));
    }

    @PostMapping("/offers")
    public ResponseEntity<PartnershipDTOs.OfferDTO> createOffer(@RequestBody PartnershipDTOs.OfferDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOffer(dto));
    }

    @PutMapping("/offers/{id}")
    public ResponseEntity<PartnershipDTOs.OfferDTO> updateOffer(@PathVariable Long id, @RequestBody PartnershipDTOs.OfferDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.PartnerInterviewDTO>> getAllInterviews() {
        return ResponseEntity.ok(service.getAllInterviews());
    }

    @GetMapping("/interviews/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerInterviewDTO> getInterviewById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInterviewById(id));
    }

    @PostMapping("/interviews")
    public ResponseEntity<PartnershipDTOs.PartnerInterviewDTO> createInterview(@RequestBody PartnershipDTOs.PartnerInterviewDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createInterview(dto));
    }

    @PutMapping("/interviews/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerInterviewDTO> updateInterview(@PathVariable Long id, @RequestBody PartnershipDTOs.PartnerInterviewDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.InterviewMeetingDTO>> getAllMeetings() {
        return ResponseEntity.ok(service.getAllMeetings());
    }

    @GetMapping("/meetings/{id}")
    public ResponseEntity<PartnershipDTOs.InterviewMeetingDTO> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMeetingById(id));
    }

    @PostMapping("/meetings")
    public ResponseEntity<PartnershipDTOs.InterviewMeetingDTO> createMeeting(@RequestBody PartnershipDTOs.InterviewMeetingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMeeting(dto));
    }

    @PutMapping("/meetings/{id}")
    public ResponseEntity<PartnershipDTOs.InterviewMeetingDTO> updateMeeting(@PathVariable Long id, @RequestBody PartnershipDTOs.InterviewMeetingDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.PartnerQuizDTO>> getAllQuizzes() {
        return ResponseEntity.ok(service.getAllQuizzes());
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerQuizDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getQuizById(id));
    }

    @PostMapping("/quizzes")
    public ResponseEntity<PartnershipDTOs.PartnerQuizDTO> createQuiz(@RequestBody PartnershipDTOs.PartnerQuizDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQuiz(dto));
    }

    @PutMapping("/quizzes/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerQuizDTO> updateQuiz(@PathVariable Long id, @RequestBody PartnershipDTOs.PartnerQuizDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.PartnerQuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerQuestionDTO> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getQuestionById(id));
    }

    @PostMapping("/questions")
    public ResponseEntity<PartnershipDTOs.PartnerQuestionDTO> createQuestion(@RequestBody PartnershipDTOs.PartnerQuestionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQuestion(dto));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<PartnershipDTOs.PartnerQuestionDTO> updateQuestion(@PathVariable Long id, @RequestBody PartnershipDTOs.PartnerQuestionDTO dto) {
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
    public ResponseEntity<List<PartnershipDTOs.QuizReponsesDTO>> getAllReponses() {
        return ResponseEntity.ok(service.getAllReponses());
    }

    @GetMapping("/reponses/{id}")
    public ResponseEntity<PartnershipDTOs.QuizReponsesDTO> getReponseById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getReponseById(id));
    }

    @PostMapping("/reponses")
    public ResponseEntity<PartnershipDTOs.QuizReponsesDTO> createReponse(@RequestBody PartnershipDTOs.QuizReponsesDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReponse(dto));
    }

    @PutMapping("/reponses/{id}")
    public ResponseEntity<PartnershipDTOs.QuizReponsesDTO> updateReponse(@PathVariable Long id, @RequestBody PartnershipDTOs.QuizReponsesDTO dto) {
        return ResponseEntity.ok(service.updateReponse(id, dto));
    }

    @DeleteMapping("/reponses/{id}")
    public ResponseEntity<Void> deleteReponse(@PathVariable Long id) {
        service.deleteReponse(id);
        return ResponseEntity.noContent().build();
    }
}
