package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.repository.AddressRepository;
import com.adeem.stockflow.service.dto.AddressDTO;
import com.adeem.stockflow.service.mapper.AddressMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Address}.
 */
@Service
@Transactional
public class AddressService {

    private static final Logger LOG = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * Save a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public AddressDTO save(AddressDTO addressDTO) {
        LOG.debug("Request to save Address : {}", addressDTO);
        Address address = addressMapper.toEntity(addressDTO);
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    /**
     * Update a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public AddressDTO update(AddressDTO addressDTO) {
        LOG.debug("Request to update Address : {}", addressDTO);
        Address address = addressMapper.toEntity(addressDTO);
        address.setIsPersisted();
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    /**
     * Partially update a address.
     *
     * @param addressDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AddressDTO> partialUpdate(AddressDTO addressDTO) {
        LOG.debug("Request to partially update Address : {}", addressDTO);

        return addressRepository
            .findById(addressDTO.getId())
            .map(existingAddress -> {
                addressMapper.partialUpdate(existingAddress, addressDTO);

                return existingAddress;
            })
            .map(addressRepository::save)
            .map(addressMapper::toDto);
    }

    /**
     * Get all the addresses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AddressDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Addresses");
        return addressRepository.findAll(pageable).map(addressMapper::toDto);
    }

    /**
     *  Get all the addresses where ClientAccount is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> findAllWhereClientAccountIsNull() {
        LOG.debug("Request to get all addresses where ClientAccount is null");
        return StreamSupport.stream(addressRepository.findAll().spliterator(), false)
            .filter(address -> address.getClientAccount() == null)
            .map(addressMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the addresses where Supplier is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> findAllWhereSupplierIsNull() {
        LOG.debug("Request to get all addresses where Supplier is null");
        return StreamSupport.stream(addressRepository.findAll().spliterator(), false)
            .filter(address -> address.getSupplier() == null)
            .map(addressMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one address by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AddressDTO> findOne(Long id) {
        LOG.debug("Request to get Address : {}", id);
        return addressRepository.findById(id).map(addressMapper::toDto);
    }

    /**
     * Delete the address by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Address : {}", id);
        addressRepository.deleteById(id);
    }
}
