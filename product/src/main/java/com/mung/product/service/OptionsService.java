package com.mung.product.service;

import com.mung.common.exception.DuplicateKeyException;
import com.mung.product.domain.Options;
import com.mung.product.domain.Product;
import com.mung.product.dto.OptionsDto.AddOptionsRequest;
import com.mung.product.repository.OptionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Options addOptions(AddOptionsRequest request) {
        Product product = productService.getProduct(request.getProductId());
        Options options = Options.builder()
            .name(request.getName())
            .price(request.getPrice())
            .build();
        options.setProduct(product);
        try {
            optionsRepository.save(options);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException();
        }

        return options;
    }
}
