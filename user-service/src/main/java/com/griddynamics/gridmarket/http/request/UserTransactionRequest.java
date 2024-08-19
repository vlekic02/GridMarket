package com.griddynamics.gridmarket.http.request;

public record UserTransactionRequest(long payer, long payee, double amount) {

}
