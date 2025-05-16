package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.PaymentReceipt;
import com.adeem.stockflow.repository.PaymentReceiptRepository;
import com.adeem.stockflow.service.dto.PaymentReceiptDTO;
import com.adeem.stockflow.service.mapper.PaymentReceiptMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.PaymentReceipt}.
 */
@Service
@Transactional
public class PaymentReceiptService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentReceiptService.class);

    private final PaymentReceiptRepository paymentReceiptRepository;

    private final PaymentReceiptMapper paymentReceiptMapper;

    public PaymentReceiptService(PaymentReceiptRepository paymentReceiptRepository, PaymentReceiptMapper paymentReceiptMapper) {
        this.paymentReceiptRepository = paymentReceiptRepository;
        this.paymentReceiptMapper = paymentReceiptMapper;
    }

    /**
     * Save a paymentReceipt.
     *
     * @param paymentReceiptDTO the entity to save.
     * @return the persisted entity.
     */
    public PaymentReceiptDTO save(PaymentReceiptDTO paymentReceiptDTO) {
        LOG.debug("Request to save PaymentReceipt : {}", paymentReceiptDTO);
        PaymentReceipt paymentReceipt = paymentReceiptMapper.toEntity(paymentReceiptDTO);
        paymentReceipt = paymentReceiptRepository.save(paymentReceipt);
        return paymentReceiptMapper.toDto(paymentReceipt);
    }

    /**
     * Update a paymentReceipt.
     *
     * @param paymentReceiptDTO the entity to save.
     * @return the persisted entity.
     */
    public PaymentReceiptDTO update(PaymentReceiptDTO paymentReceiptDTO) {
        LOG.debug("Request to update PaymentReceipt : {}", paymentReceiptDTO);
        PaymentReceipt paymentReceipt = paymentReceiptMapper.toEntity(paymentReceiptDTO);
        paymentReceipt.setIsPersisted();
        paymentReceipt = paymentReceiptRepository.save(paymentReceipt);
        return paymentReceiptMapper.toDto(paymentReceipt);
    }

    /**
     * Partially update a paymentReceipt.
     *
     * @param paymentReceiptDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PaymentReceiptDTO> partialUpdate(PaymentReceiptDTO paymentReceiptDTO) {
        LOG.debug("Request to partially update PaymentReceipt : {}", paymentReceiptDTO);

        return paymentReceiptRepository
            .findById(paymentReceiptDTO.getId())
            .map(existingPaymentReceipt -> {
                paymentReceiptMapper.partialUpdate(existingPaymentReceipt, paymentReceiptDTO);

                return existingPaymentReceipt;
            })
            .map(paymentReceiptRepository::save)
            .map(paymentReceiptMapper::toDto);
    }

    /**
     * Get all the paymentReceipts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PaymentReceiptDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PaymentReceipts");
        return paymentReceiptRepository.findAll(pageable).map(paymentReceiptMapper::toDto);
    }

    /**
     * Get one paymentReceipt by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PaymentReceiptDTO> findOne(Long id) {
        LOG.debug("Request to get PaymentReceipt : {}", id);
        return paymentReceiptRepository.findById(id).map(paymentReceiptMapper::toDto);
    }

    /**
     * Delete the paymentReceipt by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PaymentReceipt : {}", id);
        paymentReceiptRepository.deleteById(id);
    }
}
