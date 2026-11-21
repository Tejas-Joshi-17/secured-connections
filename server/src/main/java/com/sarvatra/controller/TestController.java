package com.sarvatra.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/students")
public class TestController {

    @GetMapping(path = "/{id}")
    public ResponseEntity<String> getStudentName(@PathVariable String id) {
        return new ResponseEntity<>("Tejas Joshi - " + id, HttpStatus.OK);

    }

}
