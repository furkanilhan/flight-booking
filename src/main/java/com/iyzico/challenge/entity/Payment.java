package com.iyzico.challenge.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal price;
    private String bankResponse;

    private String transactionId;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

}
