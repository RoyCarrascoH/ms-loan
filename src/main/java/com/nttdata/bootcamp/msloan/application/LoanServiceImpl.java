package com.nttdata.bootcamp.msloan.application;

import com.nttdata.bootcamp.msloan.exception.ResourceNotFoundException;
import com.nttdata.bootcamp.msloan.infrastructure.LoanRepository;
import com.nttdata.bootcamp.msloan.model.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Override
    public Flux<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Mono<Loan> findById(String idLoan) {
        return Mono.just(idLoan)
                .flatMap(loanRepository::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "IdPrestamo", idLoan)));
    }

    @Override
    public Mono<Loan> save(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Mono<Loan> update(Loan loan, String idLoan) {
        return loanRepository.findById(idLoan)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "IdLoan", idLoan)))
                .flatMap(c -> {
                    c.setIdClient(loan.getIdClient());
                    c.setLoanNumber(loan.getLoanNumber());
                    c.setLoanType(loan.getLoanType());
                    c.setLoanAmount(loan.getLoanAmount());
                    c.setCurrency(loan.getCurrency());
                    c.setNumberQuotas(loan.getNumberQuotas());
                    c.setStatus(loan.getStatus());
                    return loanRepository.save(c);
                });
    }

    @Override
    public Mono<Void> delete(String idLoan) {
        return loanRepository.findById(idLoan)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "IdLoan", idLoan)))
                .flatMap(loanRepository::delete);
    }

}
