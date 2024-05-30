package com.mung.product.service;

import com.mung.common.exception.DuplicateKeyException;
import com.mung.product.domain.Options;
import com.mung.product.dto.OptionsDto.AddOptionsRequest;
import com.mung.product.repository.OptionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionsService {

    private final OptionsRepository optionsRepository;
    private final ProductService productService;

    @Transactional
    public Options addOptions(AddOptionsRequest request) throws BadRequestException {
        Options options = Options.builder()
            .name(request.getName())
            .price(request.getPrice())
            .build();
        options.setProduct(productService.getProduct(request.getProductId()));
        try {
            optionsRepository.save(options);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException();
        }

        return options;
    }
}
