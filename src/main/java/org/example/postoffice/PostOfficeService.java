package org.example.postoffice;

import lombok.RequiredArgsConstructor;
import org.example.repositories.PostOfficeRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostOfficeService {
    private final SessionFactory sessionFactory;

    private final PostOfficeRepository officeRepository;
    private Session session;

    @PostConstruct
    void init() {
        session = sessionFactory.openSession();
    }

    public List<PostOffice> getOffices() {
        return officeRepository.findAll();
    }

    public PostOffice getOffice(Long id){
        return officeRepository.getById(id);
    }

    public PostOffice addOffice(PostOfficeOnload onload) {
        PostOffice postOffice = new PostOffice(onload.getOfficeName(),onload.getCityName());
        officeRepository.save(postOffice);
        return postOffice;
    }

    public PostOffice modifyOffice(PostOfficeOnload onload, Long id) {
        try {
            PostOffice officeFromDB = officeRepository.findById(id).orElse(null);
            officeFromDB.setOfficeName(onload.getOfficeName());
            officeFromDB.setCityName(onload.getCityName());
            officeRepository.save(officeFromDB);
            return officeFromDB;
        } catch (NullPointerException exception) {
            return null;
        }

    }

    public void deleteOffice(Long id) {
            officeRepository.deleteById(id);
    }

    public List<PostOffice> filterOffices(String field, String param){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PostOffice> departureCriteriaQuery = builder.createQuery(PostOffice.class);
        Root<PostOffice> root = departureCriteriaQuery.from(PostOffice.class);
        if(Arrays.stream(PostOffice.class.getDeclaredFields()).anyMatch(x->x.getName().equals(field))){
            departureCriteriaQuery.select(root).where(builder.equal(root.get(field),param));
            Query query = session.createQuery(departureCriteriaQuery);
            return query.getResultList();
        }
        return new ArrayList<PostOffice>(0);
    }
}
