package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ShipService {
    public void addShip(Ship ship);

    public void updateShip(Ship ship, Ship oldShip);

    public void removeShip(Long id);

    public Ship getShipById(Long id);

    public List<Ship> listShips();

    List<Ship> getShips(
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
    );

    List<Ship> sortShips(List<Ship> ships, ShipOrder order);

    List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize);

    boolean isShipValid(Ship ship);

    boolean isProdDateValid(Date prodDate);

    Double rating(Double speed, Boolean used, Date prodDate);

    boolean isParamsValid(Ship ship);

    boolean isAllParam(Ship ship);
}
