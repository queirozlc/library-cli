package com.faesa.librarycli.core.patronprofile;

import com.faesa.librarycli.core.placinghold.Hold;

import java.time.format.DateTimeFormatter;

public record HoldResponse(
        Long holdId,
        Long instanceId,
        String title,
        String datePlaced,
        Integer daysToExpire,
        Double holdFee,

        String authorName
) {

    public static HoldResponse from(Hold hold) {
        return new HoldResponse(
                hold.getId(),
                hold.getInstanceId(),
                hold.getBookTitle(),
                hold.getDatePlaced().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                hold.getExpirationDays(),
                hold.getFee(),
                hold.bookAuthorName()
        );
    }
}
