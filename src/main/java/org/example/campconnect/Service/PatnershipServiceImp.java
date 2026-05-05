package org.example.campconnect.Service;

import lombok.AllArgsConstructor;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.example.campconnect.dto.PartnershipDTOs;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class PatnershipServiceImp implements IPatnershipService {

    private static final String DEFAULT_PARTNER_PASSWORD = "Campconnect2026!";

    private ContratRepository contratRepository;
    private OfferRepository offerRepository;
    private PartnerInterviewRepository interviewRepository;
    private InterviewMeetingRepository meetingRepository;
    private PartnerQuizRepository quizRepository;
    private PartnerQuestionRepository questionRepository;
    private QuizReponsesRepository reponsesRepository;
    private UserRepository userRepository;
    private CampingRepository campingRepository;
    private PasswordEncoder passwordEncoder;
    private NotificationService notificationService;

    @Override
    public List<PartnershipDTOs.PartnerUserSummaryDTO> getPartnerUsers() {
        return userRepository.findByRole(Role.PARTNER).stream()
                .map(this::toPartnerSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PartnershipDTOs.PartnerUserSummaryDTO createPartnerUser(PartnershipDTOs.PartnerUserWriteDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email requis");
        }
        if (userRepository.findByEmail(dto.getEmail().trim()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        String raw = dto.getPassword();
        if (raw == null || raw.isBlank()) {
            raw = DEFAULT_PARTNER_PASSWORD;
        }
        if (raw.length() < 6) {
            throw new IllegalArgumentException("Mot de passe minimum 6 caractères");
        }
        User u = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail().trim())
                .password(passwordEncoder.encode(raw))
                .phone(dto.getPhone())
                .role(Role.PARTNER)
                .camping(null)
                .enabled(dto.isActif())
                .build();
        return toPartnerSummary(userRepository.save(u));
    }

    @Override
    @Transactional
    public PartnershipDTOs.PartnerUserSummaryDTO updatePartnerUser(Long id, PartnershipDTOs.PartnerUserWriteDTO dto) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (u.getRole() != Role.PARTNER) {
            throw new IllegalArgumentException("Seuls les comptes PARTNER peuvent être modifiés ici");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String em = dto.getEmail().trim();
            userRepository.findByEmail(em).ifPresent(other -> {
                if (!other.getIdUser().equals(id)) {
                    throw new IllegalArgumentException("Email déjà utilisé");
                }
            });
            u.setEmail(em);
        }
        if (dto.getFirstName() != null) u.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) u.setLastName(dto.getLastName());
        if (dto.getPhone() != null) u.setPhone(dto.getPhone());
        u.setEnabled(dto.isActif());
        String raw = dto.getPassword();
        if (raw != null && !raw.isBlank()) {
            if (raw.length() < 6) {
                throw new IllegalArgumentException("Mot de passe minimum 6 caractères");
            }
            u.setPassword(passwordEncoder.encode(raw));
        }
        return toPartnerSummary(userRepository.save(u));
    }

    @Override
    @Transactional
    public void deletePartnerUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (u.getRole() != Role.PARTNER) {
            throw new IllegalArgumentException("Seuls les comptes PARTNER peuvent être supprimés ici");
        }
        for (PartnerQuiz q : quizRepository.findByUser_IdUser(id)) {
            deleteQuiz(q.getQuizId());
        }
        for (PartnerInterview pi : interviewRepository.findByUser_IdUser(id)) {
            meetingRepository.findByInterview_InterviewId(pi.getInterviewId()).ifPresent(meetingRepository::delete);
            interviewRepository.delete(pi);
        }
        for (Camping c : campingRepository.findAll()) {
            boolean removed = c.getPartnerLinks().removeIf(partner -> partner.getIdUser().equals(id));
            if (removed) {
                campingRepository.save(c);
            }
        }
        userRepository.delete(u);
    }

    private PartnershipDTOs.PartnerUserSummaryDTO toPartnerSummary(User u) {
        PartnershipDTOs.PartnerUserSummaryDTO d = new PartnershipDTOs.PartnerUserSummaryDTO();
        d.setId(u.getIdUser());
        d.setFirstName(u.getFirstName());
        d.setLastName(u.getLastName());
        d.setEmail(u.getEmail());
        d.setPhone(u.getPhone());
        Double sum = reponsesRepository.sumGradesForPartner(u.getIdUser());
        d.setScore(sum != null ? sum : 0);
        d.setActif(u.isEnabled());
        return d;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnershipDTOs.CampingPartnershipDTO> getPartnershipCampings() {
        return campingRepository.findAll().stream()
                .map(this::toCampingPartnershipDTO)
                .collect(Collectors.toList());
    }

    private PartnershipDTOs.CampingPartnershipDTO toCampingPartnershipDTO(Camping c) {
        PartnershipDTOs.CampingPartnershipDTO d = new PartnershipDTOs.CampingPartnershipDTO();
        d.setCampingId(c.getCampingId());
        d.setName(c.getName());
        String loc = Stream.of(c.getCity(), c.getAddress())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
        d.setLocalisation(loc);
        d.setCapacite(c.getCapacite() != null ? c.getCapacite() : 0);
        d.setPartnerIds(c.getPartnerLinks().stream()
                .map(User::getIdUser)
                .sorted()
                .collect(Collectors.toList()));
        return d;
    }

    @Override
    @Transactional
    public PartnershipDTOs.CampingPartnershipDTO createCamping(PartnershipDTOs.CampingPartnershipDTO dto) {
        Camping c = new Camping();
        c.setName(dto.getName());
        c.setCity(dto.getLocalisation() != null ? dto.getLocalisation() : "");
        c.setAddress("");
        c.setPostalCode("");
        c.setCapacite(dto.getCapacite());
        c = campingRepository.save(c);
        if (dto.getPartnerIds() != null) {
            for (Long pid : dto.getPartnerIds()) {
                User u = userRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + pid));
                if (u.getRole() == Role.PARTNER) {
                    c.getPartnerLinks().add(u);
                }
            }
        }
        return toCampingPartnershipDTO(campingRepository.save(c));
    }

    @Override
    @Transactional
    public PartnershipDTOs.CampingPartnershipDTO updateCamping(Long id, PartnershipDTOs.CampingPartnershipDTO dto) {
        Camping c = campingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camping not found with id: " + id));
        c.setName(dto.getName());
        c.setCity(dto.getLocalisation() != null ? dto.getLocalisation() : "");
        if (dto.getCapacite() >= 0) {
            c.setCapacite(dto.getCapacite());
        }
        c.getPartnerLinks().clear();
        if (dto.getPartnerIds() != null) {
            for (Long pid : dto.getPartnerIds()) {
                User u = userRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + pid));
                if (u.getRole() == Role.PARTNER) {
                    c.getPartnerLinks().add(u);
                }
            }
        }
        return toCampingPartnershipDTO(campingRepository.save(c));
    }

    @Override
    @Transactional
    public void deleteCamping(Long id) {
        Camping c = campingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camping not found with id: " + id));
        for (User occupant : userRepository.findByCamping_CampingId(id)) {
            occupant.setCamping(null);
            userRepository.save(occupant);
        }
        c.getPartnerLinks().clear();
        campingRepository.save(c);
        campingRepository.delete(c);
    }

    // ════════════════════════════════════════════════════════
    //  CONTRAT
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.ContratDTO> getAllContrats() {
        return contratRepository.findAll().stream()
                .map(this::toContratDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.ContratDTO getContratById(Long id) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat not found with id: " + id));
        return toContratDTO(contrat);
    }

    @Override
    public PartnershipDTOs.ContratDTO createContrat(PartnershipDTOs.ContratDTO dto) {
        Contrat contrat = new Contrat();
        return getContratDTO(dto, contrat);
    }

    private PartnershipDTOs.ContratDTO getContratDTO(PartnershipDTOs.ContratDTO dto, Contrat contrat) {
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setCommission(dto.getCommission());
        contrat.setStatus(ContractStatus.valueOf(dto.getStatus()));
        if (dto.getOfferId() != null) {
            offer offer = offerRepository.findById(dto.getOfferId())
                    .orElseThrow(() -> new RuntimeException("Offer not found with id: " + dto.getOfferId()));
            contrat.setOffer(offer);
        }
        return toContratDTO(contratRepository.save(contrat));
    }

    @Override
    public PartnershipDTOs.ContratDTO updateContrat(Long id, PartnershipDTOs.ContratDTO dto) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat not found with id: " + id));
        return getContratDTO(dto, contrat);
    }

    @Override
    public void deleteContrat(Long id) {
        if (!contratRepository.existsById(id))
            throw new RuntimeException("Contrat not found with id: " + id);
        contratRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  OFFER
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.OfferDTO> getAllOffers() {
        return offerRepository.findAll().stream()
                .map(this::toOfferDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.OfferDTO getOfferById(Long id) {
        offer o = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        return toOfferDTO(o);
    }

    @Override
    public PartnershipDTOs.OfferDTO createOffer(PartnershipDTOs.OfferDTO dto) {
        offer o = new offer();
        o.setTitle(dto.getTitle());
        o.setDescription(dto.getDescription());
        o.setStartDate(dto.getStartDate());
        o.setEndDate(dto.getEndDate());
        o.setPrice(dto.getPrice());
        o.setStatus(OfferStatus.valueOf(dto.getStatus()));
        if (dto.getCampingId() != null) {
            Camping c = campingRepository.findById(dto.getCampingId())
                    .orElseThrow(() -> new RuntimeException("Camping not found with id: " + dto.getCampingId()));
            o.setCamping(c);
        }
        return toOfferDTO(offerRepository.save(o));
    }

    @Override
    public PartnershipDTOs.OfferDTO updateOffer(Long id, PartnershipDTOs.OfferDTO dto) {
        offer o = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        o.setTitle(dto.getTitle());
        o.setDescription(dto.getDescription());
        o.setStartDate(dto.getStartDate());
        o.setEndDate(dto.getEndDate());
        o.setPrice(dto.getPrice());
        o.setStatus(OfferStatus.valueOf(dto.getStatus()));
        if (dto.getCampingId() != null) {
            Camping c = campingRepository.findById(dto.getCampingId())
                    .orElseThrow(() -> new RuntimeException("Camping not found with id: " + dto.getCampingId()));
            o.setCamping(c);
        } else {
            o.setCamping(null);
        }
        
        offer savedOffer = offerRepository.save(o);
        if (savedOffer.getCamping() != null && savedOffer.getCamping().getPartnerLinks() != null) {
            for (User partner : savedOffer.getCamping().getPartnerLinks()) {
                notificationService.notifyStatusChange(partner.getIdUser(), "The status of your offer '" + o.getTitle() + "' has changed to: " + o.getStatus().name());
            }
        }
        
        return toOfferDTO(savedOffer);
    }

    @Override
    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id))
            throw new RuntimeException("Offer not found with id: " + id);
        offerRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  PARTNER INTERVIEW
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.PartnerInterviewDTO> getAllInterviews() {
        return interviewRepository.findAll().stream()
                .map(this::toInterviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.PartnerInterviewDTO getInterviewById(Long id) {
        PartnerInterview pi = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + id));
        return toInterviewDTO(pi);
    }

    @Override
    public PartnershipDTOs.PartnerInterviewDTO createInterview(PartnershipDTOs.PartnerInterviewDTO dto) {
        PartnerInterview pi = new PartnerInterview();
        pi.setInterviewDate(dto.getInterviewDate());
        pi.setGlobalScore(dto.getGlobalScore());
        pi.setDecision(InterviewDecision.valueOf(dto.getDecision()));
        if (dto.getUserId() != null) {
            User u = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
            pi.setUser(u);
        }
        return toInterviewDTO(interviewRepository.save(pi));
    }

    @Override
    public PartnershipDTOs.PartnerInterviewDTO updateInterview(Long id, PartnershipDTOs.PartnerInterviewDTO dto) {
        PartnerInterview pi = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + id));
        pi.setInterviewDate(dto.getInterviewDate());
        pi.setGlobalScore(dto.getGlobalScore());
        pi.setDecision(InterviewDecision.valueOf(dto.getDecision()));
        if (dto.getUserId() != null) {
            User u = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
            pi.setUser(u);
        } else {
            pi.setUser(null);
        }
        return toInterviewDTO(interviewRepository.save(pi));
    }

    @Override
    public void deleteInterview(Long id) {
        if (!interviewRepository.existsById(id))
            throw new RuntimeException("Interview not found with id: " + id);
        interviewRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  INTERVIEW MEETING
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.InterviewMeetingDTO> getAllMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.InterviewMeetingDTO getMeetingById(Long id) {
        InterviewMeeting m = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + id));
        return toMeetingDTO(m);
    }

    @Override
    public PartnershipDTOs.InterviewMeetingDTO createMeeting(PartnershipDTOs.InterviewMeetingDTO dto) {
        InterviewMeeting m = new InterviewMeeting();
        m.setMeetingDate(dto.getMeetingDate());
        m.setStartTime(dto.getStartTime());
        m.setEndTime(dto.getEndTime());
        m.setMode(InterviewMode.valueOf(dto.getMode()));
        m.setLocation(dto.getLocation());
        m.setReport(dto.getReport());
        if (dto.getInterviewId() != null) {
            PartnerInterview pi = interviewRepository.findById(dto.getInterviewId())
                    .orElseThrow(() -> new RuntimeException("Interview not found with id: " + dto.getInterviewId()));
            m.setInterview(pi);
            notificationService.notifyInterviewScheduled(pi.getUser().getIdUser(), "An interview meeting has been scheduled for " + dto.getMeetingDate() + " at " + dto.getStartTime());
        }
        return toMeetingDTO(meetingRepository.save(m));
    }

    @Override
    public PartnershipDTOs.InterviewMeetingDTO updateMeeting(Long id, PartnershipDTOs.InterviewMeetingDTO dto) {
        InterviewMeeting m = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + id));
        m.setMeetingDate(dto.getMeetingDate());
        m.setStartTime(dto.getStartTime());
        m.setEndTime(dto.getEndTime());
        m.setMode(InterviewMode.valueOf(dto.getMode()));
        m.setLocation(dto.getLocation());
        m.setReport(dto.getReport());
        if (dto.getInterviewId() != null) {
            PartnerInterview pi = interviewRepository.findById(dto.getInterviewId())
                    .orElseThrow(() -> new RuntimeException("Interview not found with id: " + dto.getInterviewId()));
            m.setInterview(pi);
        }
        return toMeetingDTO(meetingRepository.save(m));
    }

    @Override
    public void deleteMeeting(Long id) {
        if (!meetingRepository.existsById(id))
            throw new RuntimeException("Meeting not found with id: " + id);
        meetingRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  PARTNER QUIZ
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.PartnerQuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::toQuizDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.PartnerQuizDTO getQuizById(Long id) {
        PartnerQuiz q = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        return toQuizDTO(q);
    }

    @Override
    public PartnershipDTOs.PartnerQuizDTO createQuiz(PartnershipDTOs.PartnerQuizDTO dto) {
        PartnerQuiz q = new PartnerQuiz();
        q.setTitle(dto.getTitle());
        q.setMaxScore(dto.getMaxScore());
        if (dto.getUserId() != null) {
            User u = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
            q.setUser(u);
        }
        return toQuizDTO(quizRepository.save(q));
    }

    @Override
    public PartnershipDTOs.PartnerQuizDTO updateQuiz(Long id, PartnershipDTOs.PartnerQuizDTO dto) {
        PartnerQuiz q = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        q.setTitle(dto.getTitle());
        q.setMaxScore(dto.getMaxScore());
        if (dto.getUserId() != null) {
            User u = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
            q.setUser(u);
        } else {
            q.setUser(null);
        }
        return toQuizDTO(quizRepository.save(q));
    }

    @Override
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id))
            throw new RuntimeException("Quiz not found with id: " + id);
        for (PartnerQuestion pq : questionRepository.findByQuiz_QuizId(id)) {
            reponsesRepository.deleteAll(reponsesRepository.findByQuestion_QuestionId(pq.getQuestionId()));
            questionRepository.delete(pq);
        }
        quizRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  PARTNER QUESTION
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.PartnerQuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::toQuestionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.PartnerQuestionDTO getQuestionById(Long id) {
        PartnerQuestion pq = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        return toQuestionDTO(pq);
    }

    @Override
    public PartnershipDTOs.PartnerQuestionDTO createQuestion(PartnershipDTOs.PartnerQuestionDTO dto) {
        PartnerQuestion pq = new PartnerQuestion();
        pq.setLabel(dto.getLabel());
        pq.setType(QuestionType.valueOf(dto.getType()));
        pq.setWeight(dto.getWeight());
        if (dto.getQuizId() != null) {
            PartnerQuiz quiz = quizRepository.findById(dto.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + dto.getQuizId()));
            pq.setQuiz(quiz);
        }
        return toQuestionDTO(questionRepository.save(pq));
    }

    @Override
    public PartnershipDTOs.PartnerQuestionDTO updateQuestion(Long id, PartnershipDTOs.PartnerQuestionDTO dto) {
        PartnerQuestion pq = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        pq.setLabel(dto.getLabel());
        pq.setType(QuestionType.valueOf(dto.getType()));
        pq.setWeight(dto.getWeight());
        if (dto.getQuizId() != null) {
            PartnerQuiz quiz = quizRepository.findById(dto.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + dto.getQuizId()));
            pq.setQuiz(quiz);
        }
        return toQuestionDTO(questionRepository.save(pq));
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id))
            throw new RuntimeException("Question not found with id: " + id);
        reponsesRepository.deleteAll(reponsesRepository.findByQuestion_QuestionId(id));
        questionRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  QUIZ REPONSES
    // ════════════════════════════════════════════════════════

    @Override
    public List<PartnershipDTOs.QuizReponsesDTO> getAllReponses() {
        return reponsesRepository.findAll().stream()
                .map(this::toReponsesDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartnershipDTOs.QuizReponsesDTO getReponseById(Long id) {
        Quizreponses r = reponsesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reponse not found with id: " + id));
        return toReponsesDTO(r);
    }

    @Override
    public PartnershipDTOs.QuizReponsesDTO createReponse(PartnershipDTOs.QuizReponsesDTO dto) {
        Quizreponses r = new Quizreponses();
        r.setValue(dto.getValue());
        r.setGrade(dto.getGrade());
        if (dto.getQuestionId() != null) {
            PartnerQuestion pq = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + dto.getQuestionId()));
            r.setQuestion(pq);
        }
        return toReponsesDTO(reponsesRepository.save(r));
    }

    @Override
    public PartnershipDTOs.QuizReponsesDTO updateReponse(Long id, PartnershipDTOs.QuizReponsesDTO dto) {
        Quizreponses r = reponsesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reponse not found with id: " + id));
        r.setValue(dto.getValue());
        r.setGrade(dto.getGrade());
        if (dto.getQuestionId() != null) {
            PartnerQuestion pq = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + dto.getQuestionId()));
            r.setQuestion(pq);
        }
        return toReponsesDTO(reponsesRepository.save(r));
    }

    @Override
    public void deleteReponse(Long id) {
        if (!reponsesRepository.existsById(id))
            throw new RuntimeException("Reponse not found with id: " + id);
        reponsesRepository.deleteById(id);
    }

    // ════════════════════════════════════════════════════════
    //  MAPPERS
    // ════════════════════════════════════════════════════════

    private PartnershipDTOs.ContratDTO toContratDTO(Contrat c) {
        return new PartnershipDTOs.ContratDTO(
                c.getContractId(),
                c.getStartDate(),
                c.getEndDate(),
                c.getCommission(),
                c.getStatus() != null ? c.getStatus().name() : null,
                c.getOffer() != null ? c.getOffer().getOfferId() : null
        );
    }

    private PartnershipDTOs.OfferDTO toOfferDTO(offer o) {
        return new PartnershipDTOs.OfferDTO(
                o.getOfferId(),
                o.getTitle(),
                o.getDescription(),
                o.getStartDate(),
                o.getEndDate(),
                o.getPrice(),
                o.getStatus() != null ? o.getStatus().name() : null,
                o.getCamping() != null ? o.getCamping().getCampingId() : null
        );
    }

    private PartnershipDTOs.PartnerInterviewDTO toInterviewDTO(PartnerInterview pi) {
        return new PartnershipDTOs.PartnerInterviewDTO(
                pi.getInterviewId(),
                pi.getInterviewDate(),
                pi.getGlobalScore(),
                pi.getDecision() != null ? pi.getDecision().name() : null,
                pi.getUser() != null ? pi.getUser().getIdUser() : null
        );
    }

    private PartnershipDTOs.InterviewMeetingDTO toMeetingDTO(InterviewMeeting m) {
        return new PartnershipDTOs.InterviewMeetingDTO(
                m.getMeetingId(),
                m.getMeetingDate(),
                m.getStartTime(),
                m.getEndTime(),
                m.getMode() != null ? m.getMode().name() : null,
                m.getLocation(),
                m.getReport(),
                m.getInterview() != null ? m.getInterview().getInterviewId() : null
        );
    }

    private PartnershipDTOs.PartnerQuizDTO toQuizDTO(PartnerQuiz q) {
        return new PartnershipDTOs.PartnerQuizDTO(
                q.getQuizId(),
                q.getTitle(),
                q.getMaxScore(),
                q.getUser() != null ? q.getUser().getIdUser() : null
        );
    }

    private PartnershipDTOs.PartnerQuestionDTO toQuestionDTO(PartnerQuestion pq) {
        return new PartnershipDTOs.PartnerQuestionDTO(
                pq.getQuestionId(),
                pq.getLabel(),
                pq.getType() != null ? pq.getType().name() : null,
                pq.getWeight(),
                pq.getQuiz() != null ? pq.getQuiz().getQuizId() : null
        );
    }

    private PartnershipDTOs.QuizReponsesDTO toReponsesDTO(Quizreponses r) {
        return new PartnershipDTOs.QuizReponsesDTO(
                r.getResponseId(),
                r.getValue(),
                r.getGrade(),
                r.getQuestion() != null ? r.getQuestion().getQuestionId() : null
        );
    }
    @Override
    @Transactional
    public void runPartnershipScheduler() {
        System.out.println("===== PARTNERSHIP SCHEDULER START =====");
        java.util.Date now = new java.util.Date();

        int updatedContracts = 0;
        int updatedOffers = 0;
        int updatedMeetings = 0;

        // 1) Contrats: expire si fin dépassée, sinon passe en IN_PROGRESS si démarré.
        List<Contrat> contractsToSave = new java.util.ArrayList<>();
        List<Contrat> contracts = contratRepository.findAll();
        for (Contrat c : contracts) {
            ContractStatus before = c.getStatus();
            if (c.getEndDate() != null && c.getEndDate().before(now)) {
                if (before != ContractStatus.EXPIRED && before != ContractStatus.TERMINATED) {
                    c.setStatus(ContractStatus.EXPIRED);
                    if (c.getOffer() != null && c.getOffer().getCamping() != null) {
                        for (User partner : c.getOffer().getCamping().getPartnerLinks()) {
                            notificationService.notifyContractExpired(partner.getIdUser(), "Your contract for offer '" + c.getOffer().getTitle() + "' has expired.");
                        }
                    }
                }
            } else if (c.getStartDate() != null && !c.getStartDate().after(now)) {
                if (before != ContractStatus.IN_PROGRESS && before != ContractStatus.TERMINATED) {
                    c.setStatus(ContractStatus.IN_PROGRESS);
                }
            }
            if (c.getStatus() != before) {
                contractsToSave.add(c);
                updatedContracts++;
            }
        }
        if (!contractsToSave.isEmpty()) {
            contratRepository.saveAll(contractsToSave);
        }

        // 2) Offers: expire si fin dépassée.
        List<offer> offersToSave = new java.util.ArrayList<>();
        List<offer> offers = offerRepository.findAll();
        for (offer o : offers) {
            OfferStatus before = o.getStatus();
            if (o.getEndDate() != null && o.getEndDate().before(now) && before != OfferStatus.EXPIRED) {
                o.setStatus(OfferStatus.EXPIRED);
            }
            if (o.getStatus() != before) {
                offersToSave.add(o);
                updatedOffers++;
            }
        }
        if (!offersToSave.isEmpty()) {
            offerRepository.saveAll(offersToSave);
        }

        // 3) Meetings: marque MISSED si passée et report vide.
        List<InterviewMeeting> meetingsToSave = new java.util.ArrayList<>();
        List<InterviewMeeting> meetings = meetingRepository.findAll();
        for (InterviewMeeting m : meetings) {
            if (m.getMeetingDate() != null
                    && m.getMeetingDate().before(now)
                    && (m.getReport() == null || m.getReport().isBlank())) {
                m.setReport("MISSED");
                meetingsToSave.add(m);
                updatedMeetings++;
            }
        }
        if (!meetingsToSave.isEmpty()) {
            meetingRepository.saveAll(meetingsToSave);
        }

        // 4) Quiz scoring insight (lecture seule, aucun update BD ici).
        List<PartnerQuiz> quizzes = quizRepository.findAll();
        for (PartnerQuiz q : quizzes) {
            Long userId = q.getUser() != null ? q.getUser().getIdUser() : null;
            if (userId == null) continue;
            Double score = reponsesRepository.sumGradesForPartner(userId);
            if (score != null && score < 50) {
                System.out.println("Low score partner: " + q.getUser().getEmail() + " (" + score + ")");
            }
        }

        System.out.println(
                "===== PARTNERSHIP SCHEDULER END | contracts=" + updatedContracts
                        + ", offers=" + updatedOffers
                        + ", meetings=" + updatedMeetings + " ====="
        );
    }
}