package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import com.driver.services.UserService;
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

        ParkingLot parkingLot=parkingLotRepository3.findById(parkingLotId).get();
        List<Spot> spotList=parkingLot.getSpotList();
        List<Spot>emptySpots=emptySpots(spotList,numberOfWheels);

        int minAmount=Integer.MAX_VALUE;
        Spot requiredSpot = null;

        for(Spot spot:emptySpots){
            if(spot.getPricePerHour()*timeInHours<minAmount){
                requiredSpot=spot;
                minAmount=spot.getPricePerHour()*timeInHours;
            }
        }

        if(requiredSpot==null)throw new Exception("Cannot make reservation");
        User user=userRepository3.findById(userId).get();

        Reservation reservation=new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(requiredSpot);
        reservation.setUser(user);
        user.getReservationList().add(reservation);
        userRepository3.save(user);
        requiredSpot.getReservationList().add(reservation);
        requiredSpot.setOccupied(true);

        spotRepository3.save(requiredSpot);

        return reservation;
    }

    private List<Spot> emptySpots(List<Spot> spotList, Integer numberOfWheels) {
        if(numberOfWheels==2){
            List<Spot>reqlist=new ArrayList<>();

            for(Spot spot:spotList){
                if(spot.getOccupied()==false&&(spot.getSpotType()==SpotType.TWO_WHEELER||spot.getSpotType()==SpotType.FOUR_WHEELER||spot.getSpotType()==SpotType.OTHERS)){
                    reqlist.add(spot);
                }
            }

            return reqlist;
        }
        else if (numberOfWheels==4) {
            List<Spot>reqlist=new ArrayList<>();

            for(Spot spot:spotList){
                if(spot.getOccupied()==false&&(spot.getSpotType()==SpotType.FOUR_WHEELER||spot.getSpotType()==SpotType.OTHERS)){
                    reqlist.add(spot);
                }
            }

            return reqlist;
        }
        else{
            List<Spot>reqlist=new ArrayList<>();

            for(Spot spot:spotList){
                if(spot.getOccupied()==false&&spot.getSpotType()==SpotType.OTHERS){
                    reqlist.add(spot);
                }
            }
            return reqlist;
        }
    }
}
