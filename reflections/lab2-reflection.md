# Lab 2 Reflection — WalkMates

**Lab:** 2
**Pair:** Marjan Motafeghi (mamo2414), Yasaman Vallaee (yava2400)
**Repo:** [yasva/walkmates-yava2400-mamo2414](https://github.com/yasva/walkmates-yava2400-mamo2414)
**Repo commit/tag:** [Commits · yasva/walkmates-yava2400-mamo2414](https://github.com/yasva/walkmates-yava2400-mamo2414/commits/main/)

---

### 1. What we did

#### Part A — Structural Testing & Coverage

We first ran the existing tests and used JaCoCo to establish a baseline for `PricingCalculator`. The baseline was 80% line coverage and 50% branch coverage.

We inspected the uncovered paths and added tests for a free `SHELTER_VOLUNTEER` listing and a 600-minute `DOG_WALK` booking that should include the overnight surcharge. Both tests passed.

We then added a boundary test for exactly 480 minutes. According to FR-4.3, the overnight surcharge should only apply when the booking duration is greater than 480 minutes.

#### Part B — Test Optimization

We used PIT mutation testing to investigate whether the existing tests could detect small faults in the implementation.

For the booking-limit logic, we added `newSeekerAtBookingLimitShouldBeRejected()`. The test checks a `NEW` seeker who already has one active booking, which is the maximum allowed.

We also used Mockito to isolate components. For `SeekerService`, we tested successful payment, declined payment, and payment timeout. For `BookingService`, we tested a successful booking and verified that the confirmation notification was sent.

Finally, for regression test selection, we analysed the proposed `feature/weekend-surcharge` change and prioritized the existing pricing and booking tests according to how closely they were related to the change.

---

### 2. What we found

#### Part A

The most interesting result was the exact 480-minute boundary.

The 600-minute test passed and covered the overnight-surcharge branch, but the 480-minute test failed. The expected price was `716.80` SEK, while the actual price was `860.16` SEK.

The implementation used:

`>= 480`

instead of:

`> 480`

This showed us that branch coverage can show that a branch has been executed, but it does not prove that the behaviour at its boundary is correct.

#### Part B

We found a similar boundary problem in `BookingService`. The booking-limit condition used `>` instead of `>=`.

When a `NEW` seeker already had one active booking, the values were equal. The incorrect condition did not reject the new booking, so execution continued and later failed with `Unknown provider for listing`.

After changing the comparison to `>=`, the regression test passed. PIT also showed that the changed conditional-boundary mutant was killed by `newSeekerAtBookingLimitShouldBeRejected()`.

The overall mutation score increased from 64/194 killed mutants (33%) to 73/194 (38%).

---

### 3. AI use (be honest — it doesn't lower your grade)

#### Part A

We mainly used AI to help us understand the JaCoCo report and interpret line and branch coverage. At first, we focused mostly on the coverage percentages. AI helped us understand that we should also inspect `PricingCalculator` itself in the JaCoCo report to see which specific paths had not been executed.

AI also helped us calculate the expected prices for the overnight and boundary tests. We checked these calculations against the pricing rules before using them in the tests.

#### Part B

We used AI to help us understand the PIT mutation report, Mockito, and the difference between a normal test failure and evidence of a boundary fault.

One AI suggestion was initially misleading. When the booking-limit regression test failed with `Unknown provider for listing`, AI first suggested that the test might need additional provider setup. We checked the order of the validations in `BookingService` instead of directly following that suggestion.

We found that the booking-limit check happens before the provider lookup. Therefore, the `Unknown provider for listing` error was useful evidence: the incorrect `>` condition allowed execution to continue when it should already have rejected the booking.

We kept the regression test without adding unnecessary provider setup and corrected the production condition from `>` to `>=`.

---

### 4. Judgment

#### Part A

We had to decide which uncovered paths and boundaries were most important to test. JaCoCo could show us what had or had not been executed, but it could not decide whether the tested behaviour was correct.

The 480-minute case was especially important because it came directly from the wording of FR-4.3. We therefore decided that testing the exact boundary was more important than only trying to increase the coverage percentage.

#### Part B

We had to decide whether the failure in the booking-limit test was caused by a bad test setup or by the production code. PIT and AI could provide information, but we still had to inspect the control flow in `BookingService` and decide what the failure meant.

We also had to decide which tests should have the highest priority for the proposed weekend-surcharge change. We prioritized the `PricingCalculator` tests because they directly exercise the changed pricing logic. Tests for payment handling, wallet top-up, and match explanations were considered lower priority for this specific change.

---

### 5. What we'd test next

#### Part A

We would add tests for 479 and 481 minutes. Together with the existing 480-minute test, these would check both sides of the overnight-surcharge boundary.

We would also investigate the remaining uncovered paths shown by JaCoCo instead of only trying to increase the coverage percentage.

#### Part B

We would add tests specifically for the proposed weekend surcharge. The current regression tests check that the old behaviour is preserved, but they do not directly verify the new feature.

We would test a paid booking on Saturday, Sunday, and a normal weekday. We would also test how the weekend surcharge interacts with the existing overnight surcharge and verify that a free listing remains free on a weekend.
