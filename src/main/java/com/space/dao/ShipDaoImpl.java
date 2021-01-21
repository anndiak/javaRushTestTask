package com.space.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ShipDaoImpl implements ShipDao {
    private static final Logger logger = LoggerFactory.getLogger(ShipDaoImpl.class);

    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier(value = "sessionFactory")
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void addShip(Ship ship) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(ship);
        logger.info("Ship successfully saved. Ship details: "+ship);
    }
    private int getYearFromDate(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    private double  roundingToHundred(double value) {
        return Math.round(value * 100) / 100D;
    }

    public Double rating(Double speed, Boolean isUsed, Date prodDate) {
        Double k = isUsed ? 0.5 : 1;
        int prodYear = getYearFromDate(prodDate);
        return roundingToHundred((80 * speed * k) / (3019 - prodYear - 1));
    }

    @Override
    public void updateShip(Ship result) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(result);
        logger.info("Ship successfully update. Ship details: " + result);
    }



    @Override
    public void removeShip(Long id) {
        Session session = this.sessionFactory.getCurrentSession();
        Ship ship = (Ship) session.load(Ship.class, new Long(id));

        if(ship!=null){
            session.delete(ship);
        }
        logger.info("Ship successfully removed. Ship details: " + ship);
    }

    @Override
    public Ship getShipById(Long id) {
        Session session =this.sessionFactory.getCurrentSession();
        Ship ship = (Ship) session.get(Ship.class, new Long(id));
        logger.info("Ship successfully loaded. Ship details: " + ship);
        return ship;
    }

    @Override
    public List<Ship> listShips() {
        Session session = this.sessionFactory.getCurrentSession();
        List<Ship> shipList = session.createQuery("from Ship ").list();

        for(Ship ship: shipList){
            logger.info(" list: " + ship);
        }
        return shipList;
    }
}
