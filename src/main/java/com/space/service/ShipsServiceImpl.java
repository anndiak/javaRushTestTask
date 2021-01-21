package com.space.service;

import com.space.controller.ShipOrder;
import com.space.dao.ShipDao;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ShipsServiceImpl implements ShipService{
    private ShipDao shipDao;

    @Autowired
    @Qualifier(value = "shipDao")
    public void setShipDao(ShipDao shipDao) {
        this.shipDao = shipDao;
    }

    @Override
    public void addShip(Ship ship) {
        this.shipDao.addShip(ship);
    }

    @Override
    public void updateShip(Ship ship, Ship oldShip) {
        if(ship.getName() != null){
            if (isStringValid(ship.getName())){
                oldShip.setName(ship.getName());
            }
        }
        if(ship.getPlanet() != null){
            if(isStringValid(ship.getPlanet())){
                oldShip.setPlanet(ship.getPlanet());
            }
        }
        if(ship.getShipType() != null){
            oldShip.setShipType(ship.getShipType());
        }
        if(ship.getProdDate() != null){
            if(isProdDateValid(ship.getProdDate())){
                oldShip.setProdDate(ship.getProdDate());
            }
        }
        if(ship.getUsed() != null){
            oldShip.setUsed(ship.getUsed());
        }
        if(ship.getSpeed() != null){
            if(isSpeedValid(ship.getSpeed())){
                oldShip.setSpeed(ship.getSpeed());
            }
        }
        if(ship.getCrewSize() != null){
            if(isCrewSizeValid(ship.getCrewSize())){
                oldShip.setCrewSize(ship.getCrewSize());
            }
        }

            oldShip.setRating(rating(oldShip.getSpeed(), oldShip.getUsed(),oldShip.getProdDate()));
        shipDao.updateShip(oldShip);
    }

    @Override

    public void removeShip(Long id) {
        this.shipDao.removeShip(id);
    }

    @Override

    public Ship getShipById(Long id) {
        return this.shipDao.getShipById(id);
    }

    @Override
    public List<Ship> listShips() {
        return this.shipDao.listShips();
    }

    @Override
    public List<Ship> getShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
    ) {
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);
        List<Ship>list = new ArrayList<>();
        for(Ship ship : shipDao.listShips()) {
            if (name != null && !ship.getName().contains(name)) continue;
            if (planet != null && !ship.getPlanet().contains(planet)) continue;
            if (shipType != null && ship.getShipType() != shipType) continue;
            if (afterDate != null && ship.getProdDate().before(afterDate)) continue;
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) continue;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) continue;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) continue;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) continue;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) continue;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) continue;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) continue;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) continue;

            list.add(ship);
        }
        return list;
    }
    @Override
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
        if (order != null) {
            ships.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case SPEED: return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE: return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING: return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        }
        return ships;
    }

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    private boolean isCrewSizeValid(Integer crewSize) {
        final int minCrewSize = 1;
        final int maxCrewSize = 9999;
        return crewSize != null && crewSize.compareTo(minCrewSize) >= 0 && crewSize.compareTo(maxCrewSize) <= 0;
    }

    private boolean isSpeedValid(Double speed) {
        final double minSpeed = 0.01;
        final double maxSpeed = 0.99;
        return speed != null && speed.compareTo(minSpeed) >= 0 && speed.compareTo(maxSpeed) <= 0;
    }

    private boolean isStringValid(String value) {
        final int maxStringLength = 50;
        return value != null && !value.isEmpty() && value.length() <= maxStringLength;
    }

    @Override
    public boolean isProdDateValid(Date prodDate) {
        final Date startProd = getDateForYear(2800);
        final Date endProd = getDateForYear(3019);
        return prodDate != null && prodDate.after(startProd) && prodDate.before(endProd);
    }

    private Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    private int getYearFromDate(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    private Double  roundingToHundred(Double value) {
        return Math.round(value * 100) / 100D;
    }

    public Double rating(Double speed, Boolean isUsed, Date prodDate){
        Double k = isUsed? 0.5 : 1;
        int prodYear = getYearFromDate(prodDate);
        return roundingToHundred((80*speed*k)/(3019 - prodYear + 1));
    }
    @Override
    public boolean isShipValid(Ship ship) {
        return ship != null
                && isStringValid(ship.getName())
                && isStringValid(ship.getPlanet())
                && isProdDateValid(ship.getProdDate())
                && isSpeedValid(ship.getSpeed())
                && isCrewSizeValid(ship.getCrewSize());
    }
    @Override
    public boolean isParamsValid(Ship ship){
        final Date startProd = getDateForYear(2800);
        final Date endProd = getDateForYear(3019);
        if(ship.getProdDate() != null){
            if(!ship.getProdDate().after(startProd) || !ship.getProdDate().before(endProd)){
                return true;
            }
        }
       return(((ship.getName() != null) && (ship.getName().isEmpty() || ship.getName().length() > 50))
               || ((ship.getPlanet() != null) && (ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50))
               || ((ship.getCrewSize() != null) && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
               || ((ship.getSpeed() != null) && (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)));
    }


    @Override
    public boolean isAllParam(Ship ship){
        return ship.getName() != null &&
                ship.getPlanet() != null &&
                ship.getShipType() != null &&
                ship.getProdDate() != null &&
                ship.getSpeed() != null &&
                ship.getCrewSize() != null;
    }
}
