package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Reservation reservation=reservationRepository2.findById(reservationId).get();
        Payment payment=new Payment();

        if(mode.toLowerCase().equals(PaymentMode.CARD.toString().toLowerCase()))
        payment.setPaymentMode(PaymentMode.CARD);
        else if (mode.toLowerCase().equals(PaymentMode.CASH.toString().toLowerCase())) {
        payment.setPaymentMode(PaymentMode.CASH);
        }
        else if (mode.toLowerCase().equals(PaymentMode.UPI.toString().toLowerCase())) {
            payment.setPaymentMode(PaymentMode.UPI);
        }
        else {
            throw new Exception("Payment mode not detected");
        }

        int amount=reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();

        if(amountSent<amount)throw new Exception("Insufficient Amount");

        payment.setPaymentCompleted(true);

        payment.setReservation(reservation);
        reservation.setPayment(payment);
        reservation.getSpot().setOccupied(false);

        reservationRepository2.save(reservation);

        return payment;

    }
}
