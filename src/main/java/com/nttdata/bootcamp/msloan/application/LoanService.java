package com.nttdata.bootcamp.msloan.application;

import com.nttdata.bootcamp.msloan.model.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanService {

    public Flux<Loan> findAll();

    public Mono<Loan> findById(String idLoan);

    public Mono<Loan> save(Loan loan);

    public Mono<Loan> update(Loan loan, String idLoan);

    public Mono<Void> delete(String idLoan);
}
