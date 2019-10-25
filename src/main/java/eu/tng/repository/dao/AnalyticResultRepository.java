/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.repository.dao;

import eu.tng.repository.domain.AnalyticResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author eleni
 */
@Repository
public interface AnalyticResultRepository extends MongoRepository<AnalyticResult, String> {

    public Optional<AnalyticResult> findByCallbackid(String callbackid);
    public List<AnalyticResult> findAll();

}