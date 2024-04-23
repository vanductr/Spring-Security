package rikkei.academy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api.example.com/v1/admin")
public class AdminController {
    @GetMapping("/list")
    public ResponseEntity<List<String>> listResponseEntity() {
        return new ResponseEntity<>(Arrays.asList("aa", "bb", "cc"), HttpStatus.OK);
    }

}
