package com.nttdata.bootcamp.msloan.controller;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nttdata.bootcamp.msloan.application.LoanService;
import com.nttdata.bootcamp.msloan.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    @Autowired
    private LoanService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Loan>>> listLoans() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(service.findAll()));
    }

    @GetMapping("/{idLoan}")
    public Mono<ResponseEntity<Loan>> viewLoansDetails(@PathVariable("idLoan") String idLoan) {
        return service.findById(idLoan).map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> saveLoan(@Valid @RequestBody Mono<Loan> monoLoan) {
        Map<String, Object> request = new HashMap<>();
        return monoLoan.flatMap(loan -> {
            return service.save(loan).map(c -> {
                request.put("Prestamo", c);
                request.put("mensaje", "Prestamo guardado con exito");
                request.put("timestamp", new Date());
                return ResponseEntity.created(URI.create("/api/loans/".concat(c.getIdLoan())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(request);
            });
        });
    }

    @PutMapping("/{idLoan}")
    public Mono<ResponseEntity<Loan>> editLoan(@Valid @RequestBody Loan loan, @PathVariable("idLoan") String idLoan) {
        return service.update(loan, idLoan)
                .map(c -> ResponseEntity.created(URI.create("/api/loans/".concat(idLoan)))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(c));
    }

    @DeleteMapping("/{idLoan}")
    public Mono<ResponseEntity<Void>> deleteLoan(@PathVariable("idLoan") String idLoan) {
        //return service.delete(idLoan).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        return service.delete(idLoan)
                .map(c -> ResponseEntity.created(URI.create("/api/loans/".concat(idLoan)))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(c));
    }
}
