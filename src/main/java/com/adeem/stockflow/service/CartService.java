package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Cart;
import com.adeem.stockflow.repository.CartRepository;
import com.adeem.stockflow.service.dto.CartDTO;
import com.adeem.stockflow.service.mapper.CartMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Cart}.
 */
@Service
@Transactional
public class CartService {

    private static final Logger LOG = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Save a cart.
     *
     * @param cartDTO the entity to save.
     * @return the persisted entity.
     */
    public CartDTO save(CartDTO cartDTO) {
        LOG.debug("Request to save Cart : {}", cartDTO);
        Cart cart = cartMapper.toEntity(cartDTO);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    /**
     * Update a cart.
     *
     * @param cartDTO the entity to save.
     * @return the persisted entity.
     */
    public CartDTO update(CartDTO cartDTO) {
        LOG.debug("Request to update Cart : {}", cartDTO);
        Cart cart = cartMapper.toEntity(cartDTO);
        cart.setIsPersisted();
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    /**
     * Partially update a cart.
     *
     * @param cartDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CartDTO> partialUpdate(CartDTO cartDTO) {
        LOG.debug("Request to partially update Cart : {}", cartDTO);

        return cartRepository
            .findById(cartDTO.getId())
            .map(existingCart -> {
                cartMapper.partialUpdate(existingCart, cartDTO);

                return existingCart;
            })
            .map(cartRepository::save)
            .map(cartMapper::toDto);
    }

    /**
     * Get all the carts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CartDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Carts");
        return cartRepository.findAll(pageable).map(cartMapper::toDto);
    }

    /**
     * Get one cart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CartDTO> findOne(Long id) {
        LOG.debug("Request to get Cart : {}", id);
        return cartRepository.findById(id).map(cartMapper::toDto);
    }

    /**
     * Delete the cart by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Cart : {}", id);
        cartRepository.deleteById(id);
    }
}
