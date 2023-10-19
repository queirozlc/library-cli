package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.placinghold.Hold;

import java.time.format.DateTimeFormatter;

public record HoldListResponse(
        String holdId,
        String bookTitle,
        String datePlaced,
        String expirationDays,
        String fee,
        String authorName,
        String patronName,
        String patronType
) {

    public static HoldListResponse fromHold(Hold hold) {
        return new HoldListResponse(
                hold.getId().toString(),
                hold.getBookTitle(),
                hold.datePlacedFormatted(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                hold.getExpirationDays().toString(),
                hold.getFee().toString(),
                hold.bookAuthorName(),
                hold.patronName(),
                hold.patronType()
        );
    }
}
