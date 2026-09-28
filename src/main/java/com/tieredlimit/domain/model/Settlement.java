package com.tieredlimit.domain.model;

import java.util.List;

public class Settlement {
    //private TransactionKey transactionKey;
    private final Transaction transaction;
    //private Participant participant;
    private List<Apportionment> apportionments;

    public Settlement(Transaction transaction, List<Apportionment> apportionments) {
        this.transaction = transaction;
        this.apportionments = List.copyOf(apportionments);
    }

    public void replaceApportionments(List<Apportionment> newApportionments) {
        validateApportionments(newApportionments);
        this.apportionments = List.copyOf(newApportionments);
    }

    private void validateApportionments(List<Apportionment> newApportionments) {
        long discountAmount =  this.transaction.discountAmount().value();

        long sum = 0;
        for(Apportionment apportionment : newApportionments) {
            sum += apportionment.amount().value();
        }

        if(discountAmount != sum)
            throw new IllegalArgumentException("할인금액(" + discountAmount + ")은 항상 분담금 합(" + sum + ")과 일치해야합니다.");
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public List<Apportionment> getApportionments() {
        return apportionments;
    }
}