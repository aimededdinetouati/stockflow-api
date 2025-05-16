package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Attachment;
import com.adeem.stockflow.repository.AttachmentRepository;
import com.adeem.stockflow.service.dto.AttachmentDTO;
import com.adeem.stockflow.service.mapper.AttachmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Attachment}.
 */
@Service
@Transactional
public class AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentService.class);

    private final AttachmentRepository attachmentRepository;

    private final AttachmentMapper attachmentMapper;

    public AttachmentService(AttachmentRepository attachmentRepository, AttachmentMapper attachmentMapper) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
    }

    /**
     * Save a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AttachmentDTO save(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to save Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(attachment);
    }

    /**
     * Update a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AttachmentDTO update(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to update Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment.setIsPersisted();
        attachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(attachment);
    }

    /**
     * Partially update a attachment.
     *
     * @param attachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AttachmentDTO> partialUpdate(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to partially update Attachment : {}", attachmentDTO);

        return attachmentRepository
            .findById(attachmentDTO.getId())
            .map(existingAttachment -> {
                attachmentMapper.partialUpdate(existingAttachment, attachmentDTO);

                return existingAttachment;
            })
            .map(attachmentRepository::save)
            .map(attachmentMapper::toDto);
    }

    /**
     * Get all the attachments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AttachmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Attachments");
        return attachmentRepository.findAll(pageable).map(attachmentMapper::toDto);
    }

    /**
     * Get one attachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findById(id).map(attachmentMapper::toDto);
    }

    /**
     * Delete the attachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
    }
}
