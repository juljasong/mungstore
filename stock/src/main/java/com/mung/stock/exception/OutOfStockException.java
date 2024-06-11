package com.mung.stock.exception;

import com.mung.common.domain.Validate.Message;
import com.mung.common.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OutOfStockException extends CommonException {

    private static final String MESSAGE = Message.OUT_OF_STOCK;

    public OutOfStockException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
