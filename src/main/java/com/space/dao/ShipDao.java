package com.space.dao;

import com.space.model.Ship;

import java.util.List;
import java.util.Map;

public interface ShipDao {
    public void addShip(Ship ship);

    public void updateShip(Ship result);

    public void removeShip(Long id);

    public Ship getShipById(Long id);

    public List<Ship> listShips();
}
