package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Spot spot = reservation.getSpot();
        int bill = reservation.getNumberOfHours() * spot.getPricePerHour();
        if(amountSent < bill){
            throw new Exception("Insufficient Amount");
        }
        String modeOfPayment = mode.toUpperCase();
        PaymentMode paymentMode;
        try {
            paymentMode = PaymentMode.valueOf(modeOfPayment);
        }catch (Exception e){
            throw new Exception("Payment mode not detected");
        }
        // now make payment
        Payment payment = new Payment();
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(paymentMode);
        payment.setReservation(reservation);

        reservation.setPayment(payment);

        //make the spot occupied
        //spot.setOccupied(true);

        reservationRepository2.save(reservation);
        return payment;
    }
}
