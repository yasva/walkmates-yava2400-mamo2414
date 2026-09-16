package com.walkmates.lab1;

import com.walkmates.model.Seeker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Lab 1, Part B — specification-based tests for {@link Seeker}.
 *
 * <p>Design your tests on paper first (equivalence partitions, boundary values, decision table)
 * from {@code docs/REQUIREMENTS.md} FR-1.1 / FR-1.3 / FR-1.2, then implement them here. One
 * worked example is provided; the {@code TODO}s are yours.</p>
 *
 *  @author Yasaman Vallaee
 *  @author Marjan Motafeghizenoz
 */
class SeekerSpecBasedTest {

    // ---- Worked example: boundary value at the maximum single top-up (FR-1.3) ----
    @Test
    @DisplayName("Top-up exactly at the 5000 SEK single-transaction maximum is accepted")
    void topUpAtSingleMaximumIsAccepted() {
        Seeker seeker = new Seeker("sam@example.com", "Sam", "0707654321");

        seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP); // 5000.00, the boundary value

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MAX_SINGLE_TOP_UP);
    }

    @Test
    @DisplayName("Adding 250 SEK to a new seeker gives a 250.00 balance")
    void addingFundsWorks() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");  // Arrange
        seeker.addFunds(250.00);                                             // Act
        assertThat(seeker.getBalance()).isEqualTo(250.00);                   // Assert
    }

    // TODO (EP): email equivalence classes (FR-1.1)

    @Test
    @DisplayName("Valid email format and length is accepted")
    void validEmailIsAccepted() {
        // Valid equivalence class
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0707654321"
        );

        assertThat(seeker).isNotNull();
    }

    @Test
    @DisplayName("Email without @ is rejected")
    void emailWithoutAtIsRejected() {
        // Invalid equivalence class: missing @
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "samexample.com",
                        "Sam",
                        "0707654321"
                ));
    }

    @Test
    @DisplayName("Email with empty local part is rejected")
    void emailWithEmptyLocalPartIsRejected() {
        // Invalid equivalence class: empty local part
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "@example.com",
                        "Sam",
                        "0707654321"
                ));
    }

    @Test
    @DisplayName("Email with domain without dot is rejected")
    void emailWithoutDomainDotIsRejected() {
        // Invalid equivalence class: domain without dot
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "sam@example",
                        "Sam",
                        "0707654321"
                ));
    }

    @Test
    @DisplayName("Email longer than 254 characters is rejected")
    void emailLongerThan254CharactersIsRejected() {
        // Invalid equivalence class: email exceeds maximum length
        String email = "a".repeat(243) + "@example.com"; // 255 characters

        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        email,
                        "Sam",
                        "0707654321"
                ));
    }


    // TODO (EP): display name equivalence classes (FR-1.1)

    @Test
    @DisplayName("Valid display name is accepted")
    void validDisplayNameIsAccepted() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam Lee",
                "0707654321"
        );

        assertThat(seeker).isNotNull();
    }

    @Test
    @DisplayName("Display name shorter than 2 characters is rejected")
    void displayNameTooShortIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "sam@example.com",
                        "A",
                        "0707654321"
                ));
    }

    @Test
    @DisplayName("Display name longer than 40 characters is rejected")
    void displayNameTooLongIsRejected() {
        String name = "A".repeat(41);

        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "sam@example.com",
                        name,
                        "0707654321"
                ));
    }

    @Test
    @DisplayName("Display name with invalid characters is rejected")
    void displayNameWithInvalidCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "sam@example.com",
                        "Sam123",
                        "0707654321"
                ));
    }

    // TODO (EP): wallet top-up equivalence classes (FR-1.3)

    // Valid top-up representative is already covered by addingFundsWorks()
    // with a representative value of 250.00 SEK.

    @Test
    @DisplayName("Top-up below the minimum is rejected")
    void topUpBelowMinimumIsRejected() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0707654321"
        );

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(5.00));
    }

    // TODO (EP): phone number equivalence classes (FR-1.1)

    @Test
    @DisplayName("Valid Swedish phone number is accepted")
    void validSwedishPhoneNumberIsAccepted() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0701234567"
        );

        assertThat(seeker).isNotNull();
    }

    @Test
    @DisplayName("Valid international Swedish phone number is accepted")
    void validInternationalPhoneNumberIsAccepted() {
        // Valid equivalence class: international Swedish format
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "+4671234567"
        );

        assertThat(seeker).isNotNull();
    }

    @Test
    @DisplayName("Invalid phone number format is rejected")
    void invalidPhoneNumberIsRejected() {
        // Invalid equivalence class
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(
                        "sam@example.com",
                        "Sam",
                        "1234567890"
                ));
    }

    @Test
    @DisplayName("Top-up above the single-transaction maximum is rejected")
    void topUpAboveSingleMaximumIsRejected() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0707654321"
        );

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(6000.00));
    }
    
    // TODO (BVA): just-below / at / just-above the 10.00 minimum top-up (FR-1.3).

    @Test
    @DisplayName("Top-up just below 10.00 SEK is rejected")
    void topUpJustBelowMinimumIsRejected() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0707654321"
        );

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(9.99));
    }

    @Test
    @DisplayName("Top-up exactly at 10.00 SEK is accepted")
    void topUpAtMinimumIsAccepted() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0707654321"
        );

        seeker.addFunds(10.00);

        assertThat(seeker.getBalance()).isEqualTo(10.00);
    }

    @Test
    @DisplayName("Top-up just above 10.00 SEK is accepted")
    void topUpJustAboveMinimumIsAccepted() {
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam",
                "0707654321"
        );

        seeker.addFunds(10.01);

        assertThat(seeker.getBalance()).isEqualTo(10.01);
    }

    // TODO (BVA): a top-up that would push the balance above 20000.00 is rejected (FR-1.3).
    // TODO (Decision table): expected fee + max-bookings for each trust tier (FR-1.2).

}