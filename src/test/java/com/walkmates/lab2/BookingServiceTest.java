package com.walkmates.lab2;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Seeker;

import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;

import com.walkmates.service.BookingService;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PricingCalculator;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    BookingRepository bookings = mock(BookingRepository.class);
    SeekerRepository seekers = mock(SeekerRepository.class);
    ListingRepository listings = mock(ListingRepository.class);
    ProviderRepository providers = mock(ProviderRepository.class);
    PricingCalculator pricing = mock(PricingCalculator.class);
    NotificationService notifications = mock(NotificationService.class);

    @Test
    void newSeekerAtBookingLimitShouldBeRejected() {

        // Arrange
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam Lee",
                "+4671234567"
        );

        Listing listing = new Listing(
                "provider-1",
                "Dog walking",
                "Walk my dog",
                ListingType.DOG_WALK
        );

        Booking existingBooking = new Booking(
                seeker.getId(),
                "old-listing",
                60
        );

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));

        when(listings.findById(listing.getId()))
                .thenReturn(Optional.of(listing));

        when(bookings.findBySeekerId(seeker.getId()))
                .thenReturn(List.of(existingBooking));

        BookingService bookingService = new BookingService(
                seekers,
                listings,
                providers,
                bookings,
                pricing,
                notifications
        );

        // Act + Assert
        assertThatThrownBy(() ->
                bookingService.createBooking(
                        seeker.getId(),
                        listing.getId(),
                        60
                )
        )
                .hasMessageContaining("Seeker booking limit reached");
    }
}
