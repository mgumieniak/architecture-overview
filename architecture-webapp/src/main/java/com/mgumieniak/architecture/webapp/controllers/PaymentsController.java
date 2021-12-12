package com.mgumieniak.architecture.webapp.controllers;

import com.mgumieniak.architecture.models.Payment;
import com.mgumieniak.architecture.webapp.services.payments.PaymentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentService paymentService;

    @PostMapping
    public void placePayment(@RequestBody @Valid @NonNull final Payment payment){
        paymentService.place(payment);
    }
}
