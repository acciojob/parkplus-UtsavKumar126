package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        ParkingLot parkingLot;
        try {
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
        List<Spot> spotList = parkingLot.getSpotList();
        // find spot which is not occupied and spotType is larger or equal to require type.
        List<Spot> availableSpot = new ArrayList<>();
        for(Spot spot: spotList){
            if(!spot.getOccupied() && getNumOfWheels(spot.getSpotType()) >= numberOfWheels){
                availableSpot.add(spot);
            }
        }
        if(availableSpot.isEmpty()){
            throw new Exception("Cannot make reservation");
        }
        //get minimum price spot
        Spot minPriceSpot = new Spot(); //#//
        int min = Integer.MAX_VALUE;
        for (Spot spot: availableSpot){
            int price = spot.getPricePerHour();
            if(min > price){
                min = price;
                minPriceSpot = spot;
            }
        }
        // now make Reservation
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setSpot(minPriceSpot);

        //minPriceSpot.setOccupied(true);
        minPriceSpot.getReservationList().add(reservation);
        minPriceSpot.setOccupied(true);

        user.getReservationList().add(reservation);
        parkingLot.getSpotList().add(minPriceSpot);

        userRepository3.save(user);
        spotRepository3.save(minPriceSpot);
        return reservation;
    }
    public int getNumOfWheels(SpotType spotType){
        if(spotType.equals(SpotType.TWO_WHEELER)){
            return 2;
        }else if (spotType.equals(SpotType.FOUR_WHEELER)){
            return 4;
        }
        return Integer.MAX_VALUE;
    }
}
