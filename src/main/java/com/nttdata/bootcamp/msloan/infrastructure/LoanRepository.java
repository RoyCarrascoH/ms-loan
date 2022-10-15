package com.nttdata.bootcamp.msloan.infrastructure;

import com.nttdata.bootcamp.msloan.model.Loan;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LoanRepository extends ReactiveMongoRepository<Loan, String> {

}