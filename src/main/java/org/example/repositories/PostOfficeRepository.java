package org.example.repositories;

import org.example.postoffice.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {

}
