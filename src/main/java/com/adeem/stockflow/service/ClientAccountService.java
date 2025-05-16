package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.mapper.ClientAccountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final ClientAccountMapper clientAccountMapper;

    public ClientAccountService(ClientAccountRepository clientAccountRepository, ClientAccountMapper clientAccountMapper) {
        this.clientAccountRepository = clientAccountRepository;
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
        ClientAccount clientAccount = clientAccountMapper.toEntity(clientAccountDTO);
        clientAccount.setIsPersisted();
        clientAccount = clientAccountRepository.save(clientAccount);
        return clientAccountMapper.toDto(clientAccount);
    }

    /**
     * Partially update a clientAccount.
     *
     * @param clientAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClientAccountDTO> partialUpdate(ClientAccountDTO clientAccountDTO) {
        LOG.debug("Request to partially update ClientAccount : {}", clientAccountDTO);

        return clientAccountRepository
            .findById(clientAccountDTO.getId())
            .map(existingClientAccount -> {
                clientAccountMapper.partialUpdate(existingClientAccount, clientAccountDTO);

                return existingClientAccount;
            })
            .map(clientAccountRepository::save)
            .map(clientAccountMapper::toDto);
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
