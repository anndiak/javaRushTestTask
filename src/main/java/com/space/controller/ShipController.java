package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {
    private ShipService shipService;

    @Autowired(required = true)
    @Qualifier(value = "shipService")
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Ship> getAllShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {

        final List<Ship> ships = shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);

        final List<Ship> sortedShips = shipService.sortShips(ships, order);

        return shipService.getPage(sortedShips, pageNumber, pageSize);
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET)
    public int getShipCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ){
        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {

        if (!shipService.isShipValid(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getUsed() == null) ship.setUsed(false);
        ship.setSpeed(ship.getSpeed());

        final double rating = shipService.rating(ship.getSpeed(), ship.getUsed(), ship.getProdDate());
        ship.setRating(rating);

       shipService.addShip(ship);

       final int size = shipService.listShips().size() - 1;
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Ship> getShipById(@PathVariable("id") Long id){
        if(id == null || id <= 0 ){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(shipService.getShipById(id) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final Ship ship = shipService.getShipById(id);
        return new ResponseEntity<>(ship,HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public @ResponseBody
    ResponseEntity<Ship> updateShip(@PathVariable Long id, @RequestBody Ship ship){

        if (id <= 0 || shipService.isParamsValid(ship)) {
            return new ResponseEntity<Ship>(HttpStatus.BAD_REQUEST);
        }
        if(shipService.getShipById(id) == null){
            return new ResponseEntity<Ship>(HttpStatus.NOT_FOUND);
        }

            shipService.updateShip(ship,shipService.getShipById(id));

            return new ResponseEntity<Ship>(shipService.getShipById(id), HttpStatus.OK);

    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        if(id == null || id <= 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(shipService.getShipById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        shipService.removeShip(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
