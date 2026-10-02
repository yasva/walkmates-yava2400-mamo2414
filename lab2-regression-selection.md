# Lab 2 — Regression Test Selection
## Change

The `feature/weekend-surcharge` change adds a new rule to the booking price calculation. If a paid booking starts on Saturday or Sunday, a weekend surcharge of 10% of the base cost is added to the price.

A new `scheduledStartDate` is added to `Booking`. `BookingService` passes this date when a booking is created, and `PricingCalculator` uses the date to check if the booking starts on Saturday or Sunday. `BookingController` is also changed so that it can receive `scheduledStartDate` in a booking request.

If a booking does not have a scheduled date, the old pricing behaviour is kept and no weekend surcharge is added. Free listings also remain free.

## Change-relevant tests

The most relevant existing tests are the four tests in `PricingCalculatorStructuralTest`.

`shortWalkPrice()` checks the normal pricing of a paid booking. This test is important because a booking without a scheduled date should still have the same price as before.

`freeListingShouldCostZero()` checks that a free listing still costs 0.00. The new weekend surcharge should not change the price of free listings.

`overnightBookingIncludesSurcharge()` checks the existing overnight surcharge. This is relevant because the new weekend surcharge is added to the same price calculation, so the existing overnight behaviour should still work correctly.

`exactly480MinutesShouldNotIncludeSurcharge()` checks the existing 480-minute boundary behaviour. The weekend change should not change this existing behaviour.

All four tests use bookings without `scheduledStartDate`. This means that they do not test the new weekend surcharge directly. They are useful regression tests because they check that the new change does not break the existing pricing behaviour.

The two existing tests in `BookingServiceTest` are also relevant. `successfulBookingShouldSendConfirmationNotification()` checks the successful booking flow, and `newSeekerAtBookingLimitShouldBeRejected()` checks the booking-limit behaviour.

These tests use the old `createBooking` call without a scheduled date. They are useful for checking that the existing `BookingService` behaviour still works after the change. However, they do not test the weekend price calculation itself because the pricing calculator is mocked in the successful booking test.

## Prioritized test order

The first test to run is `PricingCalculatorStructuralTest.shortWalkPrice()`. The second is `PricingCalculatorStructuralTest.overnightBookingIncludesSurcharge()`. The third is `PricingCalculatorStructuralTest.exactly480MinutesShouldNotIncludeSurcharge()`. The fourth is `PricingCalculatorStructuralTest.freeListingShouldCostZero()`.

After the pricing tests, `BookingServiceTest.successfulBookingShouldSendConfirmationNotification()` should be run, followed by `BookingServiceTest.newSeekerAtBookingLimitShouldBeRejected()`.

After these change-relevant tests, the complete test suite can be run as a final regression check.

## Justification

The `PricingCalculatorStructuralTest` tests should run first because the main change is in the price calculation. These tests use the real `PricingCalculator` and can quickly show if the new change has broken existing pricing behaviour.

The normal paid booking test is run first because it checks the basic pricing path. The overnight test follows because the new weekend surcharge and the existing overnight surcharge are both part of the price calculation. The 480-minute test checks an important existing boundary, and the free-listing test checks that free bookings still remain free.

The `BookingServiceTest` tests are run after the pricing tests because `BookingService` is also changed by the new feature. The existing tests check that the old `createBooking` call still works and that existing booking behaviour is preserved. However, they do not check the new weekend calculation directly because they do not provide a scheduled date and the pricing calculator is mocked in the successful booking test.

The complete test suite is run last to check that the change has not caused unexpected problems in other parts of the system.

## Lower-priority / wasteful tests

Tests related to wallet top-up, payment handling, seeker information, match explanations, and `MatchController` have lower priority for this specific change.

For example, the tests in `SeekerServiceTest` check successful payments, declined payments, and payment timeouts. These behaviours are not changed by the weekend surcharge feature.

The tests in `MatchExplanationServiceTest` check the match explanation functionality, and `MatchControllerWebTest` checks `MatchController`, not `BookingController`. These tests do not directly exercise the changed pricing or booking code.

These tests are still useful and should be included when the complete test suite is run. However, running them before the pricing and booking tests would give less useful feedback for this specific change, so they have lower priority in this regression selection.git add lab2-regression-selection.md