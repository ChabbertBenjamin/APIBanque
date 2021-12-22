package fr.miage.bank.controller;

import fr.miage.bank.entity.Operation;

import fr.miage.bank.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;


@RestController
@RequestMapping(value = "/operations")
public class OperationController {

    private final OperationService operationService;


    public OperationController(OperationService accountService) {
        this.operationService = accountService;
    }

    @GetMapping(value = "/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("accountId") String id){
        Iterable<Operation> allOperations = operationService.findAllOperations(id);
        return ResponseEntity.ok(allOperations);
    }

    @GetMapping(value = "/operations/{operationId}")
    public ResponseEntity<?> getOperationById(@PathVariable("operationId") String operationId ){

        return Optional.ofNullable(operationService.findOperationById(operationId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(i.get()))
                .orElse(ResponseEntity.notFound().build());
    }
}