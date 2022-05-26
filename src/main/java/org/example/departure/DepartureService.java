package org.example.departure;

import lombok.RequiredArgsConstructor;
import org.example.postoffice.PostOffice;
import org.example.postoffice.PostOfficeService;
import org.example.repositories.DepartureRepository;
import org.example.repositories.PostOfficeRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartureService {
    private final SessionFactory sessionFactory;
    @Autowired
    private final DepartureRepository departureRepository;
    @Autowired
    private final PostOfficeRepository officeRepository;
    private final PostOfficeService postOfficeService;
    private Session session;

    @PostConstruct
    void init() {
        session = sessionFactory.openSession();
    }

    public List<Departure> getDepartures() {
        return departureRepository.findAll();
    }

    public Departure getDeparture(Long id) {
        return departureRepository.findById(id).orElse(null);
    }

    public Departure addDeparture(DepartureOnload onload) {
        Departure departure = new Departure(onload.getType(), onload.getDepartureDate());
        departure.setOffice(postOfficeService.getOffice(onload.getOfficeId()));
        departureRepository.save(departure);
        return departure;
    }

    public Departure modifyDeparture(DepartureOnload onload, Long id) {
        try {
            var transaction = session.beginTransaction();
            Departure departureFromDB = departureRepository.getById(id);
            departureFromDB.setType(onload.getType());
            departureFromDB.setDepartureDate(onload.getDepartureDate());
            departureRepository.save(departureFromDB);
            transaction.commit();
            return departureFromDB;
        } catch (ObjectNotFoundException exception) {
            return null;
        }

    }

    public void deleteDeparture(Long id) {
        var transaction = session.beginTransaction();
        departureRepository.deleteById(id);
        transaction.commit();
    }

    public List<Departure> filterDepartures(String field, String param) {
        switch (field) {
            case "type" -> {
                return departureRepository.findDeparturesByType(param);
            }
            case "date" -> {
                return departureRepository.findDeparturesByDepartureDate(param.replace('_','*'));
            }
            case "office" -> {
                PostOffice office = officeRepository.findById(Long.parseLong(param)).orElse(null);
                return departureRepository.findDeparturesByOffice(office);
            }
        }
        return new ArrayList<Departure>(0);

    }

}
