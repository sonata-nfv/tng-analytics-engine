/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.api.exception;

import eu.tng.graphprofiler.GPController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author eleni
 */
@RestControllerAdvice
public class ExceptionResolver {

    private static final Logger logger = Logger.getLogger(GPController.class.getName());

    @ExceptionHandler(Exception.class)
    public HashMap<String, String> handleException(HttpServletRequest request, Exception e) {
        logger.info("aaaaaaaaaaaaaaaaaa");
        HashMap<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public HashMap<String, String> handleNotFoundException(CustomNotFoundException ex) {
        logger.info("aaaaaaaaaaaaaaaaaa");
        HashMap<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }
}
