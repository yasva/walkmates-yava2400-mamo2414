package com.walkmates.lab2;

import com.walkmates.model.Seeker;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PaymentService;
import com.walkmates.service.SeekerService;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SeekerServiceTest {

    SeekerRepository seekers = mock(SeekerRepository.class);
    PaymentService payments = mock(PaymentService.class);
    NotificationService notifications = mock(NotificationService.class);

    @Test
    void successfulPaymentShouldCreditWallet() throws Exception {

        // Arrange
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam Lee",
                "+4671234567"
        );

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));

        when(payments.charge(
                seeker.getId(),
                "payment-1",
                100.00
        )).thenReturn("confirmation-1");

        when(seekers.save(seeker))
                .thenReturn(seeker);

        SeekerService seekerService =
                new SeekerService(seekers, payments, notifications);

        // Act
        seekerService.topUp(
                seeker.getId(),
                "payment-1",
                100.00
        );

        // Assert
        assertThat(seeker.getBalance()).isEqualTo(100.00);
    }

    @Test
    void declinedPaymentShouldNotCreditWallet() throws Exception {

        // Arrange
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam Lee",
                "+4671234567"
        );

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));

        when(payments.charge(
                seeker.getId(),
                "payment-1",
                100.00
        )).thenThrow(new PaymentService.PaymentException(
                "Payment declined"
        ));

        SeekerService seekerService =
                new SeekerService(seekers, payments, notifications);

        // Act + Assert
        assertThatThrownBy(() ->
                seekerService.topUp(
                        seeker.getId(),
                        "payment-1",
                        100.00
                )
        ).isInstanceOf(PaymentService.PaymentException.class);

        assertThat(seeker.getBalance()).isEqualTo(0.00);
    }

    @Test
    void paymentTimeoutShouldNotCreditWallet() throws Exception {

        // Arrange
        Seeker seeker = new Seeker(
                "sam@example.com",
                "Sam Lee",
                "+4671234567"
        );

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));

        when(payments.charge(
                seeker.getId(),
                "payment-1",
                100.00
        )).thenThrow(new PaymentService.PaymentTimeoutException(
                "Payment timeout"
        ));

        SeekerService seekerService =
                new SeekerService(seekers, payments, notifications);

        // Act + Assert
        assertThatThrownBy(() ->
                seekerService.topUp(
                        seeker.getId(),
                        "payment-1",
                        100.00
                )
        ).isInstanceOf(PaymentService.PaymentTimeoutException.class);

        assertThat(seeker.getBalance()).isEqualTo(0.00);
    }
}
