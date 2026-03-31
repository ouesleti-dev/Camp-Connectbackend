package projectexamen.spring.campconnect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import projectexamen.spring.campconnect.Entity.Role;
import projectexamen.spring.campconnect.Entity.User;
import projectexamen.spring.campconnect.Entity.campings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests unitaires — Entité campings")
class campingsTest {

    private campings camping;

    // ── helper ────────────────────────────────────────────────────────────

    private User buildUser(Long id, String email, Role role) {
        return User.builder()
                .idUser(id)
                .firstName("Alice").lastName("Dupont")
                .email(email).password("encoded")
                .phone("0600000000")
                .role(role)
                .enabled(true)
                .build();
    }

    @BeforeEach
    void setUp() {
        camping = new campings();
        camping.setCampingId(1L);
        camping.setName("Forest Camp");
        camping.setAddress("12 rue des Pins");
        camping.setCity("Lyon");
        camping.setPostalCode("69000");
        camping.setCapacite(50);
        camping.setUsers(new ArrayList<>());
        camping.setPartnerLinks(new HashSet<>());
    }

    // ════════════════════════════════════════════════════════════════════
    //  Constructeurs
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Constructeurs")
    class ConstructorTests {

        @Test
        @DisplayName("NoArgsConstructor — crée une instance vide")
        void noArgsConstructor_createsEmptyInstance() {
            campings c = new campings();
            assertThat(c).isNotNull();
            assertThat(c.getCampingId()).isNull();
            assertThat(c.getName()).isNull();
            assertThat(c.getCapacite()).isNull();
        }

        @Test
        @DisplayName("AllArgsConstructor — initialise tous les champs")
        void allArgsConstructor_initializesAllFields() {
            List<User> users = new ArrayList<>();
            Set<User> partners = new HashSet<>();
            campings c = new campings(2L, "River Camp", "5 av du lac", "Paris",
                    "75001", 100, users, partners);

            assertThat(c.getCampingId()).isEqualTo(2L);
            assertThat(c.getName()).isEqualTo("River Camp");
            assertThat(c.getAddress()).isEqualTo("5 av du lac");
            assertThat(c.getCity()).isEqualTo("Paris");
            assertThat(c.getPostalCode()).isEqualTo("75001");
            assertThat(c.getCapacite()).isEqualTo(100);
            assertThat(c.getUsers()).isSameAs(users);
            assertThat(c.getPartnerLinks()).isSameAs(partners);
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Getters / Setters
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Getters & Setters")
    class GetterSetterTests {

        @Test
        @DisplayName("setCampingId / getCampingId")
        void campingId_getterSetter() {
            camping.setCampingId(99L);
            assertThat(camping.getCampingId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("setName / getName")
        void name_getterSetter() {
            camping.setName("Mountain Camp");
            assertThat(camping.getName()).isEqualTo("Mountain Camp");
        }

        @Test
        @DisplayName("setAddress / getAddress")
        void address_getterSetter() {
            camping.setAddress("99 chemin du col");
            assertThat(camping.getAddress()).isEqualTo("99 chemin du col");
        }

        @Test
        @DisplayName("setCity / getCity")
        void city_getterSetter() {
            camping.setCity("Grenoble");
            assertThat(camping.getCity()).isEqualTo("Grenoble");
        }

        @Test
        @DisplayName("setPostalCode / getPostalCode")
        void postalCode_getterSetter() {
            camping.setPostalCode("38000");
            assertThat(camping.getPostalCode()).isEqualTo("38000");
        }

        @Test
        @DisplayName("setCapacite / getCapacite")
        void capacite_getterSetter() {
            camping.setCapacite(200);
            assertThat(camping.getCapacite()).isEqualTo(200);
        }

        @Test
        @DisplayName("setCapacite — valeur nulle acceptée")
        void capacite_nullValue() {
            camping.setCapacite(null);
            assertThat(camping.getCapacite()).isNull();
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Relation Users (OneToMany)
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Relation users (OneToMany)")
    class UsersRelationTests {

        @Test
        @DisplayName("setUsers / getUsers — liste vide par défaut")
        void users_emptyByDefault() {
            assertThat(camping.getUsers()).isEmpty();
        }

        @Test
        @DisplayName("setUsers — lie les utilisateurs au camping")
        void users_setAndGet() {
            User u1 = buildUser(1L, "a@x.com", Role.CAMPOWNER);
            User u2 = buildUser(2L, "b@x.com", Role.CAMPER);
            camping.setUsers(List.of(u1, u2));

            assertThat(camping.getUsers()).hasSize(2)
                    .extracting(User::getEmail)
                    .containsExactlyInAnyOrder("a@x.com", "b@x.com");
        }

        @Test
        @DisplayName("setUsers — remplace la liste précédente")
        void users_replace() {
            camping.setUsers(List.of(buildUser(1L, "a@x.com", Role.CAMPOWNER)));
            camping.setUsers(List.of(buildUser(2L, "b@x.com", Role.CAMPER)));
            assertThat(camping.getUsers()).hasSize(1);
            assertThat(camping.getUsers().get(0).getEmail()).isEqualTo("b@x.com");
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Relation PartnerLinks (ManyToMany)
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Relation partnerLinks (ManyToMany)")
    class PartnerLinksTests {

        @Test
        @DisplayName("partnerLinks — vide par défaut après setUp")
        void partnerLinks_emptyInitially() {
            assertThat(camping.getPartnerLinks()).isEmpty();
        }

        @Test
        @DisplayName("add — ajoute un partenaire")
        void partnerLinks_addPartner() {
            User partner = buildUser(10L, "partner@x.com", Role.PARTNER);
            camping.getPartnerLinks().add(partner);

            assertThat(camping.getPartnerLinks()).hasSize(1)
                    .extracting(User::getIdUser)
                    .containsExactly(10L);
        }

        @Test
        @DisplayName("add — Set déduplique les partenaires identiques")
        void partnerLinks_noDuplicates() {
            User partner = buildUser(10L, "partner@x.com", Role.PARTNER);
            camping.getPartnerLinks().add(partner);
            camping.getPartnerLinks().add(partner); // doublon

            assertThat(camping.getPartnerLinks()).hasSize(1);
        }

        @Test
        @DisplayName("remove — retire un partenaire")
        void partnerLinks_removePartner() {
            User p1 = buildUser(10L, "p1@x.com", Role.PARTNER);
            User p2 = buildUser(11L, "p2@x.com", Role.PARTNER);
            camping.getPartnerLinks().add(p1);
            camping.getPartnerLinks().add(p2);

            camping.getPartnerLinks().remove(p1);

            assertThat(camping.getPartnerLinks()).hasSize(1)
                    .extracting(User::getIdUser)
                    .containsExactly(11L);
        }

        @Test
        @DisplayName("clear — vide la liste des partenaires")
        void partnerLinks_clear() {
            camping.getPartnerLinks().add(buildUser(10L, "p@x.com", Role.PARTNER));
            camping.getPartnerLinks().clear();

            assertThat(camping.getPartnerLinks()).isEmpty();
        }

        @Test
        @DisplayName("setPartnerLinks — remplace l'ensemble")
        void partnerLinks_setNewSet() {
            Set<User> newSet = new HashSet<>();
            newSet.add(buildUser(20L, "new@x.com", Role.PARTNER));
            camping.setPartnerLinks(newSet);

            assertThat(camping.getPartnerLinks()).hasSize(1)
                    .extracting(User::getEmail)
                    .containsExactly("new@x.com");
        }

        @Test
        @DisplayName("removeIf — filtre les partenaires par id")
        void partnerLinks_removeIf_byId() {
            User p1 = buildUser(10L, "p1@x.com", Role.PARTNER);
            User p2 = buildUser(11L, "p2@x.com", Role.PARTNER);
            camping.getPartnerLinks().add(p1);
            camping.getPartnerLinks().add(p2);

            boolean removed = camping.getPartnerLinks()
                    .removeIf(u -> u.getIdUser().equals(10L));

            assertThat(removed).isTrue();
            assertThat(camping.getPartnerLinks()).hasSize(1);
            assertThat(camping.getPartnerLinks().iterator().next().getIdUser()).isEqualTo(11L);
        }

        @Test
        @DisplayName("removeIf — retourne false si aucun partenaire supprimé")
        void partnerLinks_removeIf_notFound() {
            camping.getPartnerLinks().add(buildUser(10L, "p@x.com", Role.PARTNER));
            boolean removed = camping.getPartnerLinks()
                    .removeIf(u -> u.getIdUser().equals(99L));
            assertThat(removed).isFalse();
            assertThat(camping.getPartnerLinks()).hasSize(1);
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Cohérence des données
    // ════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Cohérence des données")
    class DataConsistencyTests {

        @Test
        @DisplayName("camping peut avoir à la fois users et partnerLinks")
        void camping_usersAndPartners_coexist() {
            User client  = buildUser(1L, "client@x.com", Role.CAMPER);
            User partner = buildUser(2L, "partner@x.com", Role.PARTNER);
            User owner   = buildUser(3L, "owner@x.com",   Role.CAMPOWNER);

            camping.setUsers(List.of(client, owner));
            camping.getPartnerLinks().add(partner);

            assertThat(camping.getUsers()).hasSize(2);
            assertThat(camping.getPartnerLinks()).hasSize(1);
        }

        @Test
        @DisplayName("deux campings distincts ont des partnerLinks indépendants")
        void twoNewCampings_haveIndependentPartnerLinks() {
            campings c1 = new campings();
            c1.setPartnerLinks(new HashSet<>());
            campings c2 = new campings();
            c2.setPartnerLinks(new HashSet<>());

            c1.getPartnerLinks().add(buildUser(1L, "a@x.com", Role.PARTNER));

            assertThat(c2.getPartnerLinks()).isEmpty();
        }
    }
}