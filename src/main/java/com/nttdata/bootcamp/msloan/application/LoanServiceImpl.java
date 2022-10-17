package com.nttdata.bootcamp.msloan.application;

import com.nttdata.bootcamp.msloan.config.WebClientConfig;
import com.nttdata.bootcamp.msloan.dto.LoanDto;
import com.nttdata.bootcamp.msloan.exception.ResourceNotFoundException;
import com.nttdata.bootcamp.msloan.infrastructure.LoanRepository;
import com.nttdata.bootcamp.msloan.model.Client;
import com.nttdata.bootcamp.msloan.model.Loan;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
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
    public Mono<Loan> save(LoanDto loanDto) {

        return findClientByDni(String.valueOf(loanDto.getDocumentNumber()))
                        .flatMap(client -> {
                    return validateNumberClientLoan(client, loanDto, "save").flatMap(o -> {
                        return loanDto.validateFields()
                                .flatMap(at -> {
                                    if (at.equals(true)) {
                                        return loanDto.mapperToLoan(client)
                                                .flatMap(ba -> {
                                                    log.info("sg MapperToLoan-------: ");
                                                    return loanRepository.save(ba);
                                                });
                                    } else {
                                        return Mono.error(new ResourceNotFoundException("Tipo Prestamo", "LoanType", loanDto.getLoanType()));
                                    }
                                });
                    });
                });
    }

    @Override
    public Mono<Loan> update(LoanDto loanDto, String idLoan) {
        return findClientByDni(String.valueOf(loanDto.getDocumentNumber()))
                .flatMap(client -> {
                    return validateNumberClientLoan(client, loanDto, "update").flatMap(o -> {
                        return loanDto.validateFields()
                                .flatMap(at -> {
                                    if (at.equals(true)) {
                                        return loanRepository.findById(idLoan)
                                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "idLoan", idLoan)))
                                                .flatMap(x -> {
                                                    x.setClient(client);
                                                    x.setLoanNumber(loanDto.getLoanNumber());
                                                    x.setLoanType(loanDto.getLoanType());
                                                    x.setLoanAmount(loanDto.getLoanAmount());
                                                    x.setCurrency(loanDto.getCurrency());
                                                    x.setNumberQuotas(loanDto.getNumberQuotas());
                                                    x.setStatus(loanDto.getStatus());
                                                    return loanRepository.save(x);
                                                });
                                    } else {
                                        return Mono.error(new ResourceNotFoundException("Tipo Prestamo", "LoanType", loanDto.getLoanType()));
                                    }
                                });
                    });
                });
    }

    @Override
    public Mono<Void> delete(String idLoan) {
        return loanRepository.findById(idLoan)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "IdLoan", idLoan)))
                .flatMap(loanRepository::delete);
    }

    public Mono<Client> findClientByDni(String documentNumber) {
        WebClientConfig webconfig = new WebClientConfig();
        return webconfig.setUriData("http://localhost:8082/").flatMap(
                d -> {
                    return webconfig.getWebclient().get().uri("/api/clients/documentNumber/" + documentNumber).retrieve().bodyToMono(Client.class);
                }
        );
    }

    public Mono<Boolean> validateNumberClientLoan(Client client, LoanDto loanDto, String method) {
        log.info("Inicio validateNumberClientLoan-------: ");
        Boolean isOk = false;
        if (client.getClientType().equals("Personal")) {
            if (method.equals("save")) {
                Flux<Loan> list = loanRepository.findByLoanClient(client.getDocumentNumber(), loanDto.getLoanType());
                return list.count().flatMap(cant -> {
                    log.info("1 Personal cantidad : ", cant);
                    if (cant >= 1) {
                        log.info("2 Personal cantidad : ", cant);
                        return Mono.error(new ResourceNotFoundException("Cliente ", client.getClientType()));
                    } else {
                        log.info("3 Personal cantidad : ", cant);
                        return Mono.just(true);
                    }
                });
            } else {
                return Mono.just(true);
            }
        } else if (client.getClientType().equals("Business")) {
            if (method.equals("save")) {
                Flux<Loan> list = loanRepository.findByLoanClient(client.getDocumentNumber(), loanDto.getLoanType());
                return list.count().flatMap(cant -> {
                    log.info("1 Business cantidad : ", cant);
                    return Mono.just(true);
                });
            } else {
                return Mono.just(true);
            }
        } else {
            return Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", client.getClientType()));
        }
    }

}
