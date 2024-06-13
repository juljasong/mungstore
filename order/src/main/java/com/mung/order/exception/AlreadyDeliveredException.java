package com.mung.order.exception;

import com.mung.common.domain.Validate.Message;
import com.mung.common.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlreadyDeliveredException extends CommonException {

    private static final String MESSAGE = Message.ALREADY_DELIVERED;

    public AlreadyDeliveredException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
