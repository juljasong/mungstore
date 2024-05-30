package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.dto.OptionsDto.AddOptionsRequest;
import com.mung.product.service.OptionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class OptionsController {

    private final OptionsService optionsService;

    @PostMapping("/options")
    public MessageResponse<?> addOptions(@RequestBody @Valid AddOptionsRequest addOptionsRequest)
        throws BadRequestException {
        optionsService.addOptions(addOptionsRequest);

        return MessageResponse.ofSuccess();
    }
}

