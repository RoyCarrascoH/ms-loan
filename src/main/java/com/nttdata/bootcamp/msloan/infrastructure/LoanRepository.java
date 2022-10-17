package com.nttdata.bootcamp.msloan.infrastructure;

import com.nttdata.bootcamp.msloan.model.Loan;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface LoanRepository extends ReactiveMongoRepository<Loan, String> {

    @Query(value = "{'client.documentNumber' : ?0, loanType: ?1 }")
    Flux<Loan> findByLoanClient(String documentNumber, String loanType);
}