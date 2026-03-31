package projectexamen.spring.campconnect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import projectexamen.spring.campconnect.Entity.InterviewMeeting;
import projectexamen.spring.campconnect.Entity.InterviewMode;
import projectexamen.spring.campconnect.Entity.PartnerInterview;

import java.sql.Time;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests unitaires — Entité InterviewMeeting")
class InterviewMeetingTest {

    private InterviewMeeting meeting;
    private Date meetingDate;
    private Time startTime;
    private Time endTime;
    private PartnerInterview sampleInterview;

    @BeforeEach
    void setUp() {
        meetingDate    = new Date(2025 - 1900, 3, 10);  // 2025-04-10
        startTime      = Time.valueOf("09:00:00");
        endTime        = Time.valueOf("10:30:00");

        sampleInterview = new PartnerInterview();
        sampleInterview.setInterviewId(5L);

        meeting = new InterviewMeeting();
        meeting.setMeetingId(1L);
        meeting.setMeetingDate(meetingDate);
        meeting.setStartTime(startTime);
        meeting.setEndTime(endTime);

        meeting.setLocation("Zoom");
        meeting.setReport("RAS — entretien positif");
        meeting.setInterview(sampleInterview);
    }

    // ════════════════════════════════════════════════════════════════════
    //  Constructeurs
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Constructeurs")
    class ConstructorTests {

        @Test
        @DisplayName("NoArgsConstructor — crée une instance avec champs null")
        void noArgsConstructor_allFieldsNull() {
            InterviewMeeting m = new InterviewMeeting();
            assertThat(m.getMeetingId()).isNull();
            assertThat(m.getMeetingDate()).isNull();
            assertThat(m.getStartTime()).isNull();
            assertThat(m.getEndTime()).isNull();
            assertThat(m.getMode()).isNull();
            assertThat(m.getLocation()).isNull();
            assertThat(m.getReport()).isNull();
            assertThat(m.getInterview()).isNull();
        }


        // ════════════════════════════════════════════════════════════════════
        //  Getters / Setters
        // ════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("Getters & Setters")
        class GetterSetterTests {

            @Test
            @DisplayName("setMeetingId / getMeetingId")
            void meetingId_getterSetter() {
                meeting.setMeetingId(99L);
                assertThat(meeting.getMeetingId()).isEqualTo(99L);
            }

            @Test
            @DisplayName("setMeetingDate / getMeetingDate")
            void meetingDate_getterSetter() {
                Date newDate = new Date(2026 - 1900, 5, 15);
                meeting.setMeetingDate(newDate);
                assertThat(meeting.getMeetingDate()).isEqualTo(newDate);
            }

            @Test
            @DisplayName("setMeetingDate — accepte null")
            void meetingDate_null() {
                meeting.setMeetingDate(null);
                assertThat(meeting.getMeetingDate()).isNull();
            }

            @Test
            @DisplayName("setStartTime / getStartTime")
            void startTime_getterSetter() {
                Time newTime = Time.valueOf("14:00:00");
                meeting.setStartTime(newTime);
                assertThat(meeting.getStartTime()).isEqualTo(newTime);
            }

            @Test
            @DisplayName("setEndTime / getEndTime")
            void endTime_getterSetter() {
                Time newTime = Time.valueOf("15:30:00");
                meeting.setEndTime(newTime);
                assertThat(meeting.getEndTime()).isEqualTo(newTime);
            }

            @Test
            @DisplayName("setStartTime — accepte null")
            void startTime_null() {
                meeting.setStartTime(null);
                assertThat(meeting.getStartTime()).isNull();
            }


            @Test
            @DisplayName("setMode / getMode — ONSITE")
            void mode_onsite() {
                meeting.setMode(InterviewMode.ONSITE);
                assertThat(meeting.getMode()).isEqualTo(InterviewMode.ONSITE);
            }

            @Test
            @DisplayName("setMode — accepte null")
            void mode_null() {
                meeting.setMode(null);
                assertThat(meeting.getMode()).isNull();
            }

            @Test
            @DisplayName("setLocation / getLocation")
            void location_getterSetter() {
                meeting.setLocation("Google Meet");
                assertThat(meeting.getLocation()).isEqualTo("Google Meet");
            }

            @Test
            @DisplayName("setLocation — accepte null")
            void location_null() {
                meeting.setLocation(null);
                assertThat(meeting.getLocation()).isNull();
            }

            @Test
            @DisplayName("setReport / getReport")
            void report_getterSetter() {
                meeting.setReport("Candidat très motivé");
                assertThat(meeting.getReport()).isEqualTo("Candidat très motivé");
            }

            @Test
            @DisplayName("setReport — accepte null")
            void report_null() {
                meeting.setReport(null);
                assertThat(meeting.getReport()).isNull();
            }

            @Test
            @DisplayName("setInterview / getInterview — lie une PartnerInterview")
            void interview_getterSetter() {
                PartnerInterview pi = new PartnerInterview();
                pi.setInterviewId(20L);
                meeting.setInterview(pi);
                assertThat(meeting.getInterview().getInterviewId()).isEqualTo(20L);
            }

            @Test
            @DisplayName("setInterview — accepte null")
            void interview_null() {
                meeting.setInterview(null);
                assertThat(meeting.getInterview()).isNull();
            }
        }

        // ════════════════════════════════════════════════════════════════════
        //  Cohérence métier
        // ════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("Cohérence métier")
        class BusinessLogicTests {

            @Test
            @DisplayName("startTime antérieure à endTime — configuration valide")
            void startTime_beforeEndTime_isValid() {
                assertThat(meeting.getStartTime().before(meeting.getEndTime())).isTrue();
            }

            @Test
            @DisplayName("durée positive : endTime - startTime > 0")
            void duration_isPositive() {
                long duration = meeting.getEndTime().getTime() - meeting.getStartTime().getTime();
                assertThat(duration).isPositive();
            }

            @Test
            @DisplayName("mode enum — toutes les valeurs sont assignables")
            void mode_allEnumValues_assignable() {
                for (InterviewMode mode : InterviewMode.values()) {
                    meeting.setMode(mode);
                    assertThat(meeting.getMode()).isEqualTo(mode);
                }
            }

            @Test
            @DisplayName("remplacement de l'interview — met à jour la référence")
            void interview_replacement_updatesReference() {
                PartnerInterview newInterview = new PartnerInterview();
                newInterview.setInterviewId(99L);
                meeting.setInterview(newInterview);
                assertThat(meeting.getInterview().getInterviewId()).isEqualTo(99L);
            }

            @Test
            @DisplayName("Time.valueOf — format HH:mm:ss correct")
            void time_valueOf_correctFormat() {
                Time t = Time.valueOf("08:30:00");
                assertThat(t.toString()).isEqualTo("08:30:00");
            }

            @Test
            @DisplayName("modification du report n'affecte pas les autres champs")
            void report_changeDoesNotAffectOtherFields() {
                InterviewMode originalMode = meeting.getMode();
                String originalLocation = meeting.getLocation();
                meeting.setReport("Nouveau rapport");
                assertThat(meeting.getMode()).isEqualTo(originalMode);
                assertThat(meeting.getLocation()).isEqualTo(originalLocation);
            }
        }
    }

}


