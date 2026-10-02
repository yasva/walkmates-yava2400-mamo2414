# Lab 2 Analysis — WalkMates

**Pair:** Marjan Motafeghizenoz (mamo2414), Yasaman Vallaee (yava2400)

---

## Part A — Structural Testing & Coverage (M3)

### Activity 3.1 — Baseline Coverage

We ran the existing test suite and generated the JaCoCo coverage report using:

`mvn clean test jacoco:report`

For `PricingCalculator`, the baseline coverage was:

| Coverage measure | Baseline result |
| --- | --- |
| Line coverage | 80% (12/15 lines) |
| Branch coverage | 50% (5/10 branches) |

The JaCoCo report showed that the free-listing branch was not covered. The
`baseRate == 0.0` condition had been evaluated, but the path returning `0.00`
had not been executed.

The overnight-surcharge branch was also not covered. The existing test
executed the non-overnight path, but did not execute the branch that calculates
`overnightExtra`.

The report also showed that the exception path for invalid null input was not
executed.




### Activity 3.2 — Raise Branch Coverage


To increase the branch coverage of `PricingCalculator`, we added tests for the branches that were not exercised by the baseline test suite.

The existing `shortWalkPrice` test already covered a normal non-overnight booking.

We added a test for a free `SHELTER_VOLUNTEER` listing. The expected price was `0.00` because the listing has a zero base rate and the calculator returns `0.00` for free listings.

We also added a test for a 600-minute `DOG_WALK` booking to exercise the overnight-surcharge branch. The base cost was `800.00` SEK. After adding the 20% overnight surcharge, the subtotal was `960.00` SEK. The `VERIFIED` seeker has a 12% platform fee, giving a final price of `1075.20` SEK.

Both new tests passed.

After adding the structural tests, we generated the JaCoCo report again and compared the results with the baseline.

| Coverage measure | Baseline | After Activity 3.2 |
| --- | ---: | ---: |
| Line coverage | 80% | 92% |
| Branch coverage | 50% | 70% |

The new tests covered:
- the free-listing path
- the overnight-surcharge path

The resulting branch coverage increased from **50% to 70%**.



---

### Activity 3.3 — Coverage Does Not Mean Correctness

According to FR-4.3, the 20% overnight surcharge applies only when the booking duration is strictly greater than 480 minutes. We added a boundary test for a booking of exactly 480 minutes.

| Duration | Expected behaviour | Actual behaviour before fix |
| --- | --- | --- |
| 480 minutes | No overnight surcharge | Overnight surcharge was applied |

We ran:

`mvn test -Dtest=PricingCalculatorStructuralTest`

The boundary test **failed**. The expected price was `716.80` SEK, but the actual price was `860.16` SEK.

We inspected the overnight-surcharge condition in `PricingCalculator`:

`booking.getDurationMinutes() >= OVERNIGHT_THRESHOLD_MINUTES`

The implementation used `>=`, which caused the overnight surcharge to be applied when the booking duration was exactly 480 minutes.

According to FR-4.3, the correct comparison is:

`booking.getDurationMinutes() > OVERNIGHT_THRESHOLD_MINUTES`

This example demonstrates why coverage does not guarantee correctness. A test using a duration such as 600 minutes can execute the overnight-surcharge branch and therefore increase branch coverage, but it does not test the exact 480-minute boundary. The incorrect `>=` condition can therefore remain undetected even though the surcharge branch has been covered.

After observing the failing boundary test, we changed the comparison from `>=` to `>` and kept the 480-minute test as a regression test.

After the correction, `PricingCalculatorStructuralTest` passed with **4 tests, 0 failures, and 0 errors**.

The final JaCoCo results for `PricingCalculator` were:

| Coverage measure | Baseline | Final |
| --- | ---: | ---: |
| Line coverage | 80% | 92% |
| Branch coverage | 50% | 70% |


---

## Part B — Test Optimization (M4)

### Activity 4.1 — Mutation Testing

Before running PIT, we verified that the complete test suite was green.

We then ran:

`mvn clean test org.pitest:pitest-maven:mutationCoverage`

We inspected the PIT report for `PricingCalculator` and the booking-limit logic.

#### Initial mutation results

| Target | Mutant | Initial status | Reason |
| --- | --- | --- | --- |
| `PricingCalculator` | All 14 generated mutants | KILLED | The existing `PricingCalculator` tests detected all generated mutations. |
| Booking-limit logic | Boundary change `>` ↔ `>=` | Relevant boundary fault | The existing tests did not check the case where the number of active bookings was exactly equal to the maximum allowed. |

For the booking-limit logic, we added a new test called `newSeekerAtBookingLimitShouldBeRejected()`.

We added this test to check the exact boundary where the number of active bookings is equal to the maximum allowed number. We created a `NEW` seeker with one active booking. Since a `NEW` seeker can have a maximum of one active booking, another booking should be rejected.

The original condition used `>` to compare the number of active bookings with the maximum allowed number.

The new test failed because when both values were `1`, the condition `1 > 1` was false. Instead of rejecting the booking at the booking-limit check, the program continued to the next validation step.

We corrected the condition by changing `>` to `>=`. This means that the booking is rejected when the number of active bookings is equal to or greater than the maximum allowed number.

After this change, the test passed.

We then ran the complete test suite again. All **39 tests passed**, with no failures or errors.

We ran PIT again and inspected `BookingService`. The PIT report showed that the conditional-boundary mutant for the booking-limit condition was now **KILLED** by `newSeekerAtBookingLimitShouldBeRejected()`.

#### Results after additional tests

| Target | Mutant | Final status | Test that killed it |
| --- | --- | --- | --- |
| `PricingCalculator` | All 14 generated mutants | KILLED | Existing `PricingCalculator` tests |
| Booking-limit logic | Changed conditional boundary | KILLED | `newSeekerAtBookingLimitShouldBeRejected()` |

**Initial mutation score:** 64/194 killed (**33%**)  
**Final mutation score:** 73/194 killed (**38%**)

The final PIT report also showed **100% test strength (18/18)** for the tested mutants in the `com.walkmates.service` package.




---
### Activity 4.2 — Component Isolation with Mocking

We used Mockito to isolate the services from their dependencies.

#### SeekerService.topUp

We tested three different payment outcomes:

| Scenario | Payment result | Expected wallet behaviour | Result |
| --- | --- | --- | --- |
| Successful payment | Success | Wallet credited | PASS |
| Declined payment | Failure | Wallet not credited | PASS |
| Payment timeout | Timeout | Wallet not credited | PASS |

We mocked `SeekerRepository`, `PaymentService`, and `NotificationService`.

For the successful payment test, we configured `PaymentService` to return a successful payment result. After the top-up, the wallet balance increased from `0.00` to `100.00`.

For the declined payment test, we configured `PaymentService` to throw a `PaymentException`. We checked that the exception was returned and that the wallet balance stayed at `0.00`.

For the timeout test, we configured `PaymentService` to throw a `PaymentTimeoutException`. We checked that the timeout was handled as a failure and that the wallet balance stayed at `0.00`.

All three `SeekerService` tests passed.

#### BookingService

We mocked `SeekerRepository`, `ListingRepository`, `ProviderRepository`, `BookingRepository`, `PricingCalculator`, and `NotificationService`.

For a successful booking, we created the conditions needed for the booking to succeed. We then called `createBooking()` and checked that a booking was returned.

We also used Mockito `verify()` to check that `notifications.sendBookingConfirmed(seeker, booking)` was called after the successful booking.
was called after the successful booking.

**Result:** PASS. The booking was successfully created and the confirmation notification was sent.



### Activity 4.3 — Regression Selection

We analysed the proposed `feature/weekend-surcharge` change and identified the existing tests that are most relevant to the modified pricing and booking behaviour.

The complete regression-selection analysis is documented in:

`lab2-regression-selection.md`

The highest-priority tests are the existing tests in `PricingCalculatorStructuralTest` because the proposed change directly affects `PricingCalculator`.

We prioritized the tests in the following order:

1. `PricingCalculatorStructuralTest.shortWalkPrice()`
2. `PricingCalculatorStructuralTest.overnightBookingIncludesSurcharge()`
3. `PricingCalculatorStructuralTest.exactly480MinutesShouldNotIncludeSurcharge()`
4. `PricingCalculatorStructuralTest.freeListingShouldCostZero()`
5. `BookingServiceTest.successfulBookingShouldSendConfirmationNotification()`
6. `BookingServiceTest.newSeekerAtBookingLimitShouldBeRejected()`

The pricing tests are run first because they exercise the real `PricingCalculator` and therefore provide the fastest feedback about regressions in the changed pricing logic. The normal pricing path is checked first, followed by the existing overnight surcharge, the 480-minute boundary, and the free-listing behaviour.

The `BookingServiceTest` tests are run after the pricing tests because `BookingService` is also affected by the proposed change. These tests help verify that the existing booking flow still works, although they do not directly test the new weekend surcharge because they use bookings without `scheduledStartDate`, and the successful booking test mocks `PricingCalculator`.

Tests for wallet top-up, payment handling, match explanations, and `MatchController` are lower priority for this specific change because they do not directly exercise the modified pricing or booking logic. Running them for every small pricing change would provide slower feedback with less change-specific information.

However, these tests are not unnecessary in general. They are still included when the complete test suite is run as the final regression check.

An important limitation of the selected existing tests is that they do not directly test the new weekend surcharge. They mainly verify that the existing pricing and booking behaviour is preserved. New feature-specific tests would therefore still be needed to verify the Saturday/Sunday surcharge itself.


