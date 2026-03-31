package org.example.campconnect.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Données de démonstration : partenaires, campings (liens), offres, contrats,
 * entretiens, rencontres, quiz & réponses (somme des notes = score partenaire et graphiques).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeedRunner implements ApplicationRunner {

    public static final String SEED_ADMIN_EMAIL = "admin@campconnect.local";
    public static final String SEED_DEMO_PASSWORD = "Campconnect2026!";

    private final CampingRepository campingsRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final ContratRepository contratRepository;
    private final PartnerQuizRepository quizRepository;
    private final PartnerQuestionRepository questionRepository;
    private final QuizReponsesRepository reponsesRepository;
    private final PartnerInterviewRepository interviewRepository;
    private final InterviewMeetingRepository meetingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.db.seed:true}")
    private boolean seedEnabled;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.info("app.db.seed=false — aucune donnée de démo insérée.");
            return;
        }
        if (userRepository.findByEmail(SEED_ADMIN_EMAIL).isPresent()) {
            log.info("Jeu de démo déjà présent ({}), skip.", SEED_ADMIN_EMAIL);
            return;
        }

        String enc = passwordEncoder.encode(SEED_DEMO_PASSWORD);

        Camping cPins = camping("Camping Les Pins Bleus", "45 route forestière", "Arcachon", "33120", 140);
        Camping cLac = camping("Lac Tranquille", "Chemin du lac 12", "Annecy", "74000", 95);
        Camping cDune = camping("Les Dunes d’Argent", "Avenue de la plage", "Sète", "34200", 200);
        Camping cVerdon = camping("Verdon Nature", "RN 85 km 12", "Moustiers-Sainte-Marie", "04360", 110);
        Camping cBretagne = camping("Armor Bretagne", "Lieu-dit Ker Garen", "Carnac", "56340", 175);
        Camping cVosges = camping("Sapins des Vosges", "18 rue des Crêtes", "Gérardmer", "88400", 88);
        campingsRepository.saveAll(List.of(cPins, cLac, cDune, cVerdon, cBretagne, cVosges));

        User admin = User.builder().firstName("Admin").lastName("Campconnect").email(SEED_ADMIN_EMAIL)
                .password(enc).phone("+33 5 56 00 01").role(Role.ADMIN).camping(null).enabled(true).build();
        User owner = User.builder().firstName("Marie").lastName("Dupont").email("proprietaire@campconnect.local")
                .password(enc).phone("+33 5 56 00 02").role(Role.CAMPOWNER).camping(cPins).enabled(true).build();

        List<User> partners = new ArrayList<>();
        partners.add(partner(enc, "Jean", "Martin", "jean.martin@partenaire.demo", "+33 6 12 34 56 78"));
        partners.add(partner(enc, "Sophie", "Bernard", "sophie.bernard@partenaire.demo", "+33 6 98 76 54 32"));
        partners.add(partner(enc, "Lucas", "Petit", "lucas.petit@partenaire.demo", "+33 7 11 22 33 44"));
        partners.add(partner(enc, "Amélie", "Roux", "amelie.roux@partenaire.demo", "+33 6 22 33 44 55"));
        partners.add(partner(enc, "Thomas", "Girard", "thomas.girard@partenaire.demo", "+33 6 33 44 55 66"));
        partners.add(partner(enc, "Léa", "Fontaine", "lea.fontaine@partenaire.demo", "+33 6 44 55 66 77"));
        partners.add(partner(enc, "Hugo", "Mercier", "hugo.mercier@partenaire.demo", "+33 6 55 66 77 88"));
        partners.add(partner(enc, "Clara", "Durand", "clara.durand@partenaire.demo", "+33 6 66 77 88 99"));

        userRepository.save(admin);
        userRepository.save(owner);
        partners = userRepository.saveAll(partners);

        User p0 = partners.get(0);
        User p1 = partners.get(1);
        User p2 = partners.get(2);
        User p3 = partners.get(3);
        User p4 = partners.get(4);
        User p5 = partners.get(5);
        User p6 = partners.get(6);
        User p7 = partners.get(7);

        link(cPins, p0, p1, p2);
        link(cLac, p1, p3);
        link(cDune, p4, p5, p6);
        link(cVerdon, p0, p7);
        link(cBretagne, p2, p3, p4);
        link(cVosges, p6, p7);
        campingsRepository.saveAll(List.of(cPins, cLac, cDune, cVerdon, cBretagne, cVosges));

        offer o1 = offer("Partenariat haute saison 2026", "Mise en avant multicanal et tarifs préférentiels groupes.",
                2026, 4, 1, 2026, 9, 30, 4500, OfferStatus.ACCEPTED);
        offer o2 = offer("Pack services numériques", "Paiement en ligne, appli mobile, support 6 mois.",
                2026, 1, 10, 2026, 12, 31, 1200, OfferStatus.PROPOSED);
        offer o3 = offer("Éco‑certification et signalétique", "Audit éco, panneaux recyclés, médiation locale.",
                2026, 5, 1, 2026, 10, 15, 2800, OfferStatus.ACCEPTED);
        offer o4 = offer("Location matériel glamping", "Tonnelles, mobilier design, éclairage.",
                2026, 3, 1, 2026, 8, 31, 950, OfferStatus.PROPOSED);
        offer o5 = offer("Newsletter sponsorisée", "4 envois dédiés grandes agglomérations.",
                2026, 2, 1, 2026, 4, 30, 600, OfferStatus.REFUSED);
        offer o6 = offer("Entretien espaces verts premium", "Tonte, haies, arrosage connecté.",
                2026, 4, 15, 2026, 11, 30, 3200, OfferStatus.ACCEPTED);
        offer o7 = offer("Offre dernière minute hiver", "Push notifications et carte dynamique.",
                2026, 11, 1, 2027, 3, 31, 1800, OfferStatus.PROPOSED);
        List<offer> offers = offerRepository.saveAll(List.of(o1, o2, o3, o4, o5, o6, o7));

        contrat(ud(2026, 4, 1), ud(2026, 9, 30), 12.5, ContractStatus.IN_PROGRESS, offers.get(0));
        contrat(ud(2026, 2, 1), ud(2026, 8, 31), 8.5, ContractStatus.IN_PROGRESS, offers.get(0));
        contrat(ud(2026, 5, 10), ud(2026, 10, 10), 10.0, ContractStatus.IN_PROGRESS, offers.get(2));
        contrat(ud(2026, 4, 20), ud(2026, 11, 25), 9.0, ContractStatus.IN_PROGRESS, offers.get(5));
        contrat(ud(2026, 1, 15), ud(2026, 12, 15), 7.0, ContractStatus.IN_PROGRESS, offers.get(1));
        contrat(ud(2026, 3, 5), ud(2026, 7, 31), 11.0, ContractStatus.IN_PROGRESS, offers.get(3));
        contrat(ud(2026, 11, 5), ud(2027, 3, 15), 6.5, ContractStatus.IN_PROGRESS, offers.get(6));
        contrat(ud(2025, 6, 1), ud(2025, 12, 31), 5.0, ContractStatus.EXPIRED, offers.get(2));
        contrat(ud(2026, 2, 1), ud(2026, 3, 31), 4.0, ContractStatus.TERMINATED, offers.get(4));

        // Quiz / réponses — plusieurs quiz par partenaires clés pour graphiques « par quiz »
        PartnerQuiz q01 = buildQuiz(p0, "Audit qualité 2026 — Martin", 100);
        addThreeGraded(q01);

        PartnerQuiz q02 = buildQuiz(p0, "Suivi trimestriel Q1 — Martin", 60);
        addTwoOpen(q02, 19.0, 23.0);

        PartnerQuiz q03 = buildQuiz(p0, "Charte RSE — Martin", 40);
        addYesNoGraded(q03, 36.0);

        PartnerQuiz q11 = buildQuiz(p1, "Évaluation opérationnelle — Bernard", 80);
        addMixedThree(q11);

        PartnerQuiz q21 = buildQuiz(p2, "Onboarding — Petit", 50);
        addNotePair(q21, 22.0, 26.0);

        PartnerQuiz q31 = buildQuiz(p3, "Conformité accueil — Roux", 55);
        addThreeGradedScaled(q31, 14, 16, 17);

        PartnerQuiz q41 = buildQuiz(p4, "Logistique événements — Girard", 70);
        addTwoOpen(q41, 30.0, 28.0);

        PartnerQuiz q51 = buildQuiz(p5, "Marketing local — Fontaine", 45);
        addYesNoGraded(q51, 40.0);

        PartnerQuiz q61 = buildQuiz(p6, "Services families — Mercier", 65);
        addThreeGradedScaled(q61, 18, 20, 21);

        PartnerQuiz q71 = buildQuiz(p7, "Partenariats institutions — Durand", 55);
        addMcqSingle(q71, 48.0);

        seedInterview(p0, 2026, 3, 10, 82.0, InterviewDecision.VALIDATE,
                2026, 3, 14, 10, 0, 10, 50, InterviewMode.VIDEO,
                "Visio constructive : engagement sur délais de réponse et photos d’emplacements.",
                "Lien visio interne");
        seedInterview(p0, 2026, 1, 8, 68.0, InterviewDecision.TO_IMPROVE,
                2026, 1, 12, 14, 30, 15, 0, InterviewMode.PHONE,
                "Points à renforcer sur le dossier administratif ; relance sous 15 jours.",
                "Appel sortant");
        seedInterview(p1, 2026, 3, 5, 74.0, InterviewDecision.VALIDATE,
                2026, 3, 7, 9, 15, 10, 15, InterviewMode.IN_PERSON,
                "Compte rendu présentiel : validation du planning saisonnier.",
                "Siège Campconnect — salle B");
        seedInterview(p2, 2026, 2, 20, 61.0, InterviewDecision.TO_IMPROVE,
                2026, 2, 22, 11, 0, 11, 45, InterviewMode.VIDEO,
                "À compléter : indicateurs de satisfaction visiteurs.",
                "Teams — invitation envoyée");
        seedInterview(p3, 2026, 4, 2, 88.0, InterviewDecision.VALIDATE,
                2026, 4, 4, 16, 0, 17, 0, InterviewMode.IN_PERSON,
                "Excellent alignement sur la charte qualité.",
                "Camping partenaire — bureau accueil");
        seedInterview(p4, 2026, 2, 11, 55.0, InterviewDecision.REFUSE,
                2026, 2, 12, 10, 30, 11, 0, InterviewMode.PHONE,
                "Refus motivé : charge industrielle incompatible sur le créneau demandé.",
                "Standard téléphonique");
        seedInterview(p5, 2026, 3, 18, 71.0, InterviewDecision.VALIDATE,
                2026, 3, 19, 13, 45, 14, 30, InterviewMode.VIDEO,
                "Validation sous réserve de mise à jour des tarifs publics.",
                "Meet — standard entreprise");
        seedInterview(p6, 2026, 1, 25, 59.0, InterviewDecision.TO_IMPROVE,
                2026, 1, 28, 15, 30, 16, 0, InterviewMode.PHONE,
                "Structuration du réseau partenaire local à prolonger.",
                "Conférence téléphonique");
        seedInterview(p7, 2026, 4, 8, 77.0, InterviewDecision.VALIDATE,
                2026, 4, 10, 9, 0, 9, 40, InterviewMode.IN_PERSON,
                "Rencontre terrain concluante ; protocole sécurité validé.",
                "Parking Nord — point de rendez-vous");

        log.info("Données de démo insérées (admin: {}). Mot de passe : DatabaseSeedRunner.SEED_DEMO_PASSWORD",
                SEED_ADMIN_EMAIL);
    }

    private static Camping camping(String name, String address, String city, String postal, int cap) {
        Camping c = new Camping();
        c.setName(name);
        c.setAddress(address);
        c.setCity(city);
        c.setPostalCode(postal);
        c.setCapacite(cap);
        return c;
    }

    private static void link(Camping c, User... ps) {
        for (User p : ps) {
            c.getPartnerLinks().add(p);
        }
    }

    private static User partner(String enc, String fn, String ln, String mail, String phone) {
        return User.builder().firstName(fn).lastName(ln).email(mail).password(enc).phone(phone)
                .role(Role.PARTNER).camping(null).enabled(true).build();
    }

    private static offer offer(String title, String desc, int y1, int m1, int d1, int y2, int m2, int d2,
                               double price, OfferStatus status) {
        offer o = new offer();
        o.setTitle(title);
        o.setDescription(desc);
        o.setStartDate(ud(y1, m1, d1));
        o.setEndDate(ud(y2, m2, d2));
        o.setPrice(price);
        o.setStatus(status);
        return o;
    }

    private static java.util.Date ud(int y, int m, int d) {
        return Date.valueOf(LocalDate.of(y, m, d));
    }

    private void contrat(java.util.Date d1, java.util.Date d2, double commission, ContractStatus st, offer of) {
        Contrat c = new Contrat();
        c.setStartDate(d1);
        c.setEndDate(d2);
        c.setCommission(commission);
        c.setStatus(st);
        c.setOffer(of);
        contratRepository.save(c);
    }

    private PartnerQuiz buildQuiz(User partner, String title, double maxScore) {
        PartnerQuiz q = new PartnerQuiz();
        q.setTitle(title);
        q.setMaxScore(maxScore);
        q.setUser(partner);
        return quizRepository.save(q);
    }

    private PartnerQuestion q(PartnerQuiz quiz, String label, QuestionType type, double weight) {
        PartnerQuestion pq = new PartnerQuestion();
        pq.setLabel(label);
        pq.setType(type);
        pq.setWeight(weight);
        pq.setQuiz(quiz);
        return questionRepository.save(pq);
    }

    private void resp(PartnerQuestion question, String value, double grade) {
        Quizreponses r = new Quizreponses();
        r.setValue(value);
        r.setGrade(grade);
        r.setQuestion(question);
        reponsesRepository.save(r);
    }

    private void addThreeGraded(PartnerQuiz quiz) {
        PartnerQuestion a = q(quiz, "Canal de contact privilégié avec les campeurs ?", QuestionType.MCQ, 30);
        PartnerQuestion b = q(quiz, "Proposition d’amélioration pour la saison ", QuestionType.OPEN, 40);
        PartnerQuestion c = q(quiz, "Acceptation charte qualité ?", QuestionType.YES_NO, 30);
        resp(a, "Email + PMS", 27.0);
        resp(b, "Borne d’accueil avec QR et FAQ multilingue.", 35.0);
        resp(c, "OUI", 29.0);
    }

    private void addTwoOpen(PartnerQuiz quiz, double g1, double g2) {
        PartnerQuestion a = q(quiz, "Synthèse des actions prioritaires", QuestionType.OPEN, 50);
        PartnerQuestion b = q(quiz, "Risques identifiés et mitigation", QuestionType.OPEN, 50);
        resp(a, "Renfort équipe week-end et formation accueil.", g1);
        resp(b, "Stock EPI ; plan B hébergement.", g2);
    }

    private void addYesNoGraded(PartnerQuiz quiz, double grade) {
        PartnerQuestion a = q(quiz, "Validation charte environnement", QuestionType.YES_NO, 100);
        resp(a, "OUI", grade);
    }

    private void addMixedThree(PartnerQuiz quiz) {
        PartnerQuestion a = q(quiz, "Niveau satisfaction estimé (échelle interne)", QuestionType.NOTE, 35);
        PartnerQuestion b = q(quiz, "Commentaire libre", QuestionType.OPEN, 35);
        PartnerQuestion c = q(quiz, "Respect procédures sécurité", QuestionType.YES_NO, 30);
        resp(a, "7.5", 28.0);
        resp(b, "Équipe motivée, postes d’incendie à revoir.", 26.0);
        resp(c, "OUI", 24.0);
    }

    private void addNotePair(PartnerQuiz quiz, double g1, double g2) {
        PartnerQuestion a = q(quiz, "Note accueil", QuestionType.NOTE, 50);
        PartnerQuestion b = q(quiz, "Note propreté", QuestionType.NOTE, 50);
        resp(a, "8", g1);
        resp(b, "9", g2);
    }

    private void addThreeGradedScaled(PartnerQuiz quiz, int g1, int g2, int g3) {
        PartnerQuestion a = q(quiz, "Checklist accueil", QuestionType.MCQ, 34);
        PartnerQuestion b = q(quiz, "Adaptations PMR", QuestionType.OPEN, 33);
        PartnerQuestion c = q(quiz, "Assurance RC pro à jour", QuestionType.YES_NO, 33);
        resp(a, "Conforme", (double) g1);
        resp(b, "2 sanitaires adaptés ; 3ème prévu T3.", (double) g2);
        resp(c, "OUI", (double) g3);
    }

    private void addMcqSingle(PartnerQuiz quiz, double grade) {
        PartnerQuestion a = q(quiz, "Périmètre institutionnel prioritaire", QuestionType.MCQ, 100);
        resp(a, "Collectivités + offices de tourisme", grade);
    }

    private void seedInterview(
            User partner,
            int iy, int im, int id, double global, InterviewDecision decision,
            int my, int mm, int md, int sh, int sm, int eh, int em, InterviewMode mode,
            String report, String location) {
        PartnerInterview pi = new PartnerInterview();
        pi.setInterviewDate(ud(iy, im, id));
        pi.setGlobalScore(global);
        pi.setDecision(decision);
        pi.setUser(partner);
        pi = interviewRepository.save(pi);

        InterviewMeeting m = new InterviewMeeting();
        m.setMeetingDate(ud(my, mm, md));
        m.setStartTime(Time.valueOf(LocalTime.of(sh, sm)));
        m.setEndTime(Time.valueOf(LocalTime.of(eh, em)));
        m.setMode(mode);
        m.setLocation(location);
        m.setReport(report);
        m.setInterview(pi);
        m = meetingRepository.save(m);
        pi.setMeeting(m);
        interviewRepository.save(pi);
    }
}