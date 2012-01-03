/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devnexus.ting.core.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.devnexus.ting.common.SystemInformationUtils;
import com.devnexus.ting.core.dao.EventDao;
import com.devnexus.ting.core.dao.OrganizerDao;
import com.devnexus.ting.core.dao.PresentationDao;
import com.devnexus.ting.core.dao.SpeakerDao;
import com.devnexus.ting.core.model.Event;
import com.devnexus.ting.core.model.Organizer;
import com.devnexus.ting.core.model.Presentation;
import com.devnexus.ting.core.model.Speaker;
import com.devnexus.ting.core.service.BusinessService;

/**
 *
 * @author Gunnar Hillert
 * @since 1.0
 */
@Service("businessService")
public class BusinessServiceImpl implements BusinessService {

    /**
     *   Initialize Logging.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BusinessServiceImpl.class);

    @Autowired private EventDao        eventDao;
    @Autowired private OrganizerDao    organizerDao;
    @Autowired private PresentationDao presentationDao;
    @Autowired private SpeakerDao      speakerDao;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteEvent(Event event) {
        Assert.notNull(event, "The provided event must not be null.");
        Assert.notNull(event.getId(), "Id must not be Null for event " + event);

        LOGGER.debug("Deleting Event {}", event);
        eventDao.remove(event);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteOrganizer(Organizer organizerFromDb) {

        Assert.notNull(organizerFromDb,         "The provided organizer must not be null.");
        Assert.notNull(organizerFromDb.getId(), "Id must not be Null for organizer " + organizerFromDb);

        LOGGER.debug("Deleting Organizer {}", organizerFromDb);
        organizerDao.remove(organizerFromDb);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deletePresentation(Presentation presentation) {

        Assert.notNull(presentation,         "The provided presentation must not be null.");
        Assert.notNull(presentation.getId(), "Id must not be Null for presentation " + presentation);

        LOGGER.debug("Deleting Presentation {}", presentation);

        presentationDao.remove(presentation);

    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteSpeaker(Speaker speaker) {

        Assert.notNull(speaker,         "The provided speaker must not be null.");
        Assert.notNull(speaker.getId(), "Id must not be Null for speaker " + speaker);

        LOGGER.debug("Deleting Speaker {}", speaker);

        speakerDao.remove(speaker);
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> getAllEventsOrderedByName() {
        return eventDao.getAllEventsOrderedByName();
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> getAllNonCurrentEvents() {
        return eventDao.getAllNonCurrentEvents();
    }

    /** {@inheritDoc} */
    @Override
    public List<Organizer> getAllOrganizers() {
        return organizerDao.getAllOrganizers();
    }

    /** {@inheritDoc} */
    @Override
    public List<Presentation> getAllPresentations() {
        return presentationDao.getAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<Speaker> getAllSpeakersOrderedByName() {
        return speakerDao.getAllSpeakersOrderedByName();
    }

    /** {@inheritDoc} */
    @Override
    public Event getEvent(Long id) {
        return eventDao.get(id);
    }

    /** {@inheritDoc} */
    @Override
    public Event getEventByEventKey(String eventKey) {
        return eventDao.getByEventKey(eventKey);
    }

    /** {@inheritDoc} */
    @Override
    public Organizer getOrganizer(final Long organizerId) {
        return organizerDao.get(organizerId);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Organizer getOrganizerWithPicture(Long organizerId) {
        return organizerDao.getOrganizerWithPicture(organizerId);
    }

    /** {@inheritDoc} */
    @Override
    public Presentation getPresentation(Long id) {
        return presentationDao.get(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Presentation> getPresentationsForCurrentEvent() {
        return presentationDao.getPresentationsForCurrentEvent();
    }

    /** {@inheritDoc} */
    @Override
    public List<Presentation> getPresentationsForEvent(Long eventId) {
        return presentationDao.getPresentationsForEvent(eventId);
    }

    /** {@inheritDoc} */
    @Override
    public Speaker getSpeaker(Long speakerId) {
        return speakerDao.get(speakerId);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly=false)
    public byte[] getSpeakerImage(Long speakerId) {

        Assert.notNull(speakerId, "SpeakerId must not be null.");

        final Speaker speaker = getSpeaker(speakerId);

        final byte[] speakerPicture;

        if (speaker==null || speaker.getPicture() == null) {
            speakerPicture = SystemInformationUtils.getSpeakerImage(null);
        } else {
            speakerPicture = speaker.getPicture().getFileData();
        }

        return speakerPicture;

    }

    /** {@inheritDoc} */
    @Override
    public List<Speaker> getSpeakersForCurrentEvent() {
        return speakerDao.getSpeakersForCurrentEvent();
    }

    /** {@inheritDoc} */
    @Override
    public List<Speaker> getSpeakersForEvent(Long eventId) {
        return speakerDao.getSpeakersForEvent(eventId);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void saveEvent(Event event) {
        eventDao.save(event);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Organizer saveOrganizer(Organizer organizer) {
        return organizerDao.save(organizer);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void savePresentation(Presentation presentation) {
        presentationDao.save(presentation);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Speaker saveSpeaker(Speaker speaker) {
        return speakerDao.save(speaker);
    }

}