package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.PaymentConfiguration;
import com.adeem.stockflow.repository.PaymentConfigurationRepository;
import com.adeem.stockflow.service.dto.PaymentConfigurationDTO;
import com.adeem.stockflow.service.mapper.PaymentConfigurationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.PaymentConfiguration}.
 */
@Service
@Transactional
public class PaymentConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentConfigurationService.class);

    private final PaymentConfigurationRepository paymentConfigurationRepository;

    private final PaymentConfigurationMapper paymentConfigurationMapper;

    public PaymentConfigurationService(
        PaymentConfigurationRepository paymentConfigurationRepository,
        PaymentConfigurationMapper paymentConfigurationMapper
    ) {
        this.paymentConfigurationRepository = paymentConfigurationRepository;
        this.paymentConfigurationMapper = paymentConfigurationMapper;
    }

    /**
     * Save a paymentConfiguration.
     *
     * @param paymentConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    public PaymentConfigurationDTO save(PaymentConfigurationDTO paymentConfigurationDTO) {
        LOG.debug("Request to save PaymentConfiguration : {}", paymentConfigurationDTO);
        PaymentConfiguration paymentConfiguration = paymentConfigurationMapper.toEntity(paymentConfigurationDTO);
        paymentConfiguration = paymentConfigurationRepository.save(paymentConfiguration);
        return paymentConfigurationMapper.toDto(paymentConfiguration);
    }

    /**
     * Update a paymentConfiguration.
     *
     * @param paymentConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    public PaymentConfigurationDTO update(PaymentConfigurationDTO paymentConfigurationDTO) {
        LOG.debug("Request to update PaymentConfiguration : {}", paymentConfigurationDTO);
        PaymentConfiguration paymentConfiguration = paymentConfigurationMapper.toEntity(paymentConfigurationDTO);
        paymentConfiguration.setIsPersisted();
        paymentConfiguration = paymentConfigurationRepository.save(paymentConfiguration);
        return paymentConfigurationMapper.toDto(paymentConfiguration);
    }

    /**
     * Partially update a paymentConfiguration.
     *
     * @param paymentConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PaymentConfigurationDTO> partialUpdate(PaymentConfigurationDTO paymentConfigurationDTO) {
        LOG.debug("Request to partially update PaymentConfiguration : {}", paymentConfigurationDTO);

        return paymentConfigurationRepository
            .findById(paymentConfigurationDTO.getId())
            .map(existingPaymentConfiguration -> {
                paymentConfigurationMapper.partialUpdate(existingPaymentConfiguration, paymentConfigurationDTO);

                return existingPaymentConfiguration;
            })
            .map(paymentConfigurationRepository::save)
            .map(paymentConfigurationMapper::toDto);
    }

    /**
     * Get all the paymentConfigurations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PaymentConfigurationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PaymentConfigurations");
        return paymentConfigurationRepository.findAll(pageable).map(paymentConfigurationMapper::toDto);
    }

    /**
     * Get one paymentConfiguration by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PaymentConfigurationDTO> findOne(Long id) {
        LOG.debug("Request to get PaymentConfiguration : {}", id);
        return paymentConfigurationRepository.findById(id).map(paymentConfigurationMapper::toDto);
    }

    /**
     * Delete the paymentConfiguration by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PaymentConfiguration : {}", id);
        paymentConfigurationRepository.deleteById(id);
    }
}
