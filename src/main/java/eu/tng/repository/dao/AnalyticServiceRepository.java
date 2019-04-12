/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.repository.dao;

import eu.tng.repository.domain.AnalyticService;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author eleni
 */
@Repository
public interface AnalyticServiceRepository extends MongoRepository<AnalyticService, String> {

    public AnalyticService findByName(String firstName);
    public List<AnalyticService> findAll();

}