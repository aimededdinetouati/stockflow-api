package com.adeem.stockflow.service;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.mapper.ClientAccountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.ClientAccount}.
 */
@Service
@Transactional
public class ClientAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAccountService.class);

    private final ClientAccountRepository clientAccountRepository;
    private final AddressService addressService;

    private final ClientAccountMapper clientAccountMapper;

    public ClientAccountService(
        ClientAccountRepository clientAccountRepository,
        AddressService addressService,
        ClientAccountMapper clientAccountMapper
    ) {
        this.clientAccountRepository = clientAccountRepository;
        this.addressService = addressService;
        this.clientAccountMapper = clientAccountMapper;
    }

    /**
     * Save a clientAccount.
     *
     * @param clientAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientAccountDTO save(ClientAccountDTO clientAccountDTO) {
        LOG.debug("Request to save ClientAccount : {}", clientAccountDTO);
        ClientAccount clientAccount = clientAccountMapper.toEntity(clientAccountDTO);
        clientAccount = clientAccountRepository.save(clientAccount);
        return clientAccountMapper.toDto(clientAccount);
    }

    /**
     * Update a clientAccount.
     *
     * @param clientAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientAccountDTO update(ClientAccountDTO clientAccountDTO) {
        LOG.debug("Request to update ClientAccount : {}", clientAccountDTO);

        ClientAccount existing = clientAccountRepository
            .findById(clientAccountDTO.getId())
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        existing.setCompanyName(clientAccountDTO.getCompanyName());
        existing.setPhone(clientAccountDTO.getPhone());
        existing.setEmail(clientAccountDTO.getEmail());
        existing.setFax(clientAccountDTO.getFax());
        existing.setWebsite(clientAccountDTO.getWebsite());
        existing.setTaxIdentifier(clientAccountDTO.getTaxIdentifier());
        existing.setRegistrationArticle(clientAccountDTO.getRegistrationArticle());
        existing.setStatisticalId(clientAccountDTO.getStatisticalId());
        existing.setCommercialRegistry(clientAccountDTO.getCommercialRegistry());
        existing.setBankAccount(clientAccountDTO.getBankAccount());
        existing.setBankName(clientAccountDTO.getBankName());
        existing.setSocialCapital(clientAccountDTO.getSocialCapital());

        if (clientAccountDTO.getAddress() != null) {
            Address address = existing.getAddress() != null ? existing.getAddress() : new Address();
            Address savedAddress = addressService.save(address, clientAccountDTO.getAddress());
            existing.setAddress(savedAddress);
        }

        existing.setIsPersisted();
        existing = clientAccountRepository.save(existing);
        return clientAccountMapper.toDto(existing);
    }

    /**
     * Get all the clientAccounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientAccountDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ClientAccounts");
        return clientAccountRepository.findAll(pageable).map(clientAccountMapper::toDto);
    }

    /**
     * Get one clientAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClientAccountDTO> findOne(Long id) {
        LOG.debug("Request to get ClientAccount : {}", id);
        return clientAccountRepository.findById(id).map(clientAccountMapper::toDto);
    }

    /**
     * Delete the clientAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ClientAccount : {}", id);
        clientAccountRepository.deleteById(id);
    }
}
