/*
 * Copyright 2002-2016 the original author or authors.
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
package com.devnexus.ting.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author Gunnar Hillert
 */
@XmlRootElement(name="schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScheduleItemList implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer numberOfSessions;
	private Integer numberOfKeynoteSessions;
	private Integer numberOfBreakoutSessions;
	private Integer numberOfSpeakersAssigned;
	private Integer numberOfUnassignedSessions;

	private Integer numberOfBreaks;
	private Integer numberOfRooms;
	private SortedSet<Date> days;

	@XmlElement(name="scheduleItems")
	private List<ScheduleItem> scheduleItems;

	private List<Long> favoriteScheduleItemIds;

	@XmlElement(name="headerItems")
	private List<ScheduleItem> headerItems;
	private transient Map<Date, List<ScheduleItem>> headerItemsByDate;

	@XmlElement(name="registrationItems")
	private List<ScheduleItem> registrationItems;
	private transient Map<Date, List<ScheduleItem>> registrationItemsByDate;

	@XmlElement(name="breakoutItems")
	private List<ScheduleItem> breakoutItems;

	public List<Long> getFavoriteScheduleItemIds() {
		return favoriteScheduleItemIds;
	}

	public void setFavoriteScheduleItemIds(List<Long> favoriteScheduleItemIds) {
		this.favoriteScheduleItemIds = favoriteScheduleItemIds;
	}

	private transient Map<Date, List<ScheduleItem>> breakoutItemsByDate;

	public SortedSet<Date> getDays() {
		return days;
	}

	public void setDays(SortedSet<Date> days) {
		this.days = days;
	}

	public Integer getNumberOfSessions() {
		return numberOfSessions;
	}

	public void setNumberOfSessions(Integer numberOfSessions) {
		this.numberOfSessions = numberOfSessions;
	}

	public Integer getNumberOfKeynoteSessions() {
		return numberOfKeynoteSessions;
	}

	public void setNumberOfKeynoteSessions(Integer numberOfKeynoteSessions) {
		this.numberOfKeynoteSessions = numberOfKeynoteSessions;
	}

	public Integer getNumberOfBreakoutSessions() {
		return numberOfBreakoutSessions;
	}

	public void setNumberOfBreakoutSessions(Integer numberOfBreakoutSessions) {
		this.numberOfBreakoutSessions = numberOfBreakoutSessions;
	}

	public Integer getNumberOfSpeakersAssigned() {
		return numberOfSpeakersAssigned;
	}

	public void setNumberOfSpeakersAssigned(Integer numberOfSpeakersAssigned) {
		this.numberOfSpeakersAssigned = numberOfSpeakersAssigned;
	}

	public Integer getNumberOfUnassignedSessions() {
		return numberOfUnassignedSessions;
	}

	public void setNumberOfUnassignedSessions(Integer numberOfUnassignedSessions) {
		this.numberOfUnassignedSessions = numberOfUnassignedSessions;
	}

	public Integer getNumberOfBreaks() {
		return numberOfBreaks;
	}

	public void setNumberOfBreaks(Integer numberOfBreaks) {
		this.numberOfBreaks = numberOfBreaks;
	}

	public Integer getNumberOfRooms() {
		return numberOfRooms;
	}

	public void setNumberOfRooms(Integer numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}

	public List<ScheduleItem> getScheduleItems() {
		return scheduleItems;
	}

	public void setScheduleItems(List<ScheduleItem> scheduleItems) {
		this.scheduleItems = scheduleItems;
	}

	public List<ScheduleItem> getRegistrationItems() {
		if (registrationItems == null) {
			registrationItems = new ArrayList<ScheduleItem>();
			for (ScheduleItem item : scheduleItems) {
				if (isRegistrationItem(item)) {
					registrationItems.add(item);
				}
			}
		}
		return registrationItems;
	}

	public List<ScheduleItem> getHeaderItems() {
		if (headerItems == null) {
			headerItems = new ArrayList<ScheduleItem>();
			for (ScheduleItem item : scheduleItems) {
				if (isHeaderItem(item)) {
				 headerItems.add(item);
				}
			}
		}
		return headerItems;
	}

	public List<ScheduleItem> getBreakoutItems() {
		if (breakoutItems == null) {
			breakoutItems = new ArrayList<ScheduleItem>();
			for (ScheduleItem item : scheduleItems) {
				if (isBreakoutItem(item)) {
					breakoutItems.add(item);
				}
			}
		}
		return breakoutItems;
	}

	public boolean isHeaderItem(ScheduleItem item) {
		return !ScheduleItemType.SESSION.equals(item.getScheduleItemType()) && !ScheduleItemType.BREAK.equals(item.getScheduleItemType());
	}

	public boolean isRegistrationItem(ScheduleItem item) {
		return ScheduleItemType.REGISTRATION.equals(item.getScheduleItemType());
	}

	public boolean isMorningReceptionItem(ScheduleItem item) {
		return item.getScheduleItemType().equals(ScheduleItemType.MORNING_RECEPTION);
	}

	public boolean isBreakoutItem(ScheduleItem item) {
		return ScheduleItemType.SESSION.equals(item.getScheduleItemType());
	}

	public List<ScheduleItem> findRegistrationItemsOnDate(Date search) {
		if (registrationItems == null) {
			registrationItemsByDate = new HashMap<>();
			for (Date date : days) {
				for (ScheduleItem item : getRegistrationItems()) {
					List<ScheduleItem> items = registrationItemsByDate.get(date);
					if (items == null) {
						items = new ArrayList<ScheduleItem>(getRegistrationItems().size());
						registrationItemsByDate.put(date, items);
					}
					if (item.getFromTime().getDate() == date.getDate()) {
						items.add(item);
					}
				}
			}
		}
		return registrationItemsByDate.get(search);
	}

	public List<ScheduleItem> findHeaderItemsOnDate(Date search) {
		if (headerItemsByDate == null) {
			headerItemsByDate = new HashMap<Date, List<ScheduleItem>>(scheduleItems.size());
			for (Date date : days) {
				for (ScheduleItem item : getHeaderItems()) {
					List<ScheduleItem> items = headerItemsByDate.get(date);
					if (items == null) {
						items = new ArrayList<ScheduleItem>(getHeaderItems().size());
						headerItemsByDate.put(date, items);
					}

					if (item.getFromTime().getDate() == date.getDate()) {
						items.add(item);
					}
				}
			}
		}
		return headerItemsByDate.get(search);
	}

	public List<ScheduleItem> findRegistrationItemsWithoutReceptionItemsOnDate(Date search) {
		final List<ScheduleItem> registrationItemsWithoutReceptionItems = new ArrayList<>();

		for (ScheduleItem item : findRegistrationItemsOnDate(search)) {
			if (item.getTitle() != null && item.getTitle().startsWith("Reception")) {

			}
			else {
				registrationItemsWithoutReceptionItems.add(item);
			}
		}

		return registrationItemsWithoutReceptionItems;

	}

	public List<ScheduleItem> findMorningReceptionItemsOnDate(Date search) {
		final List<ScheduleItem> morningReceptionItems = new ArrayList<>();

		for (ScheduleItem item : findRegistrationItemsOnDate(search)) {
			if (item.getTitle() != null && item.getTitle().startsWith("Reception")) {
				morningReceptionItems.add(item);
			}
		}

		return morningReceptionItems;

	}

	public List<ScheduleItem> findEveningReceptionItemsOnDate(Date search) {
		final List<ScheduleItem> eveningReceptionItems = new ArrayList<>();

		for (ScheduleItem item : findHeaderItemsOnDate(search)) {
			if (ScheduleItemType.EVENING_RECEPTION.equals(item.getScheduleItemType())) {
				eveningReceptionItems.add(item);
			}
		}

		return eveningReceptionItems;
	}

	public List<ScheduleItem> findBreakItemsOnDate(Date search) {
		final List<ScheduleItem> breakItems = new ArrayList<>();

		for (ScheduleItem item : scheduleItems) {
			if ((search.getDate() == item.getFromTime().getDate()) && ScheduleItemType.BREAK.equals(item.getScheduleItemType())) {
				breakItems.add(item);
			}
		}
		return breakItems;
	}

	public String findColorForBreakItemsOnDate(Date search) {

		for (ScheduleItem item : scheduleItems) {
			if ((search.getDate() == item.getFromTime().getDate()) && ScheduleItemType.BREAK.equals(item.getScheduleItemType())) {
				if (item.getRoom() != null) {
					return item.getRoom().getColor();
				}
			}
		}

		return "#ffffff";
	}

	public List<ScheduleItem> findLunchItemsOnDate(Date search) {
		final List<ScheduleItem> breakItems = new ArrayList<>();

		for (ScheduleItem item : scheduleItems) {
			if ((search.getDate() == item.getFromTime().getDate()) && ScheduleItemType.BREAK.equals(item.getScheduleItemType())) {
				if ("lunch".equals(item.getTitle().toLowerCase()) || "dessert".equals(item.getTitle().toLowerCase())) {
					breakItems.add(item);
				}

			}
		}
		return breakItems;
	}
	public List<ScheduleItem> findBreakoutItemsOnDate(Date search) {
		if (breakoutItemsByDate == null) {
			breakoutItemsByDate = new HashMap<Date, List<ScheduleItem>>(scheduleItems.size());
			for (Date date : days) {
				for (ScheduleItem item : getBreakoutItems()) {
					List<ScheduleItem> items = breakoutItemsByDate.get(date);
					if (items == null) {
						items = new ArrayList<ScheduleItem>(getBreakoutItems().size());
						breakoutItemsByDate.put(date, items);
					}

					if (item.getFromTime().getDate() == date.getDate()) {
						items.add(item);
					}
				}
			}
		}
		return breakoutItemsByDate.get(search);
	}

	public SortedSet<Room> findRooms(Date date) {

		SortedSet<Room>rooms = new TreeSet<Room>();
		for (ScheduleItem item : findBreakoutItemsOnDate(date)) {
			rooms.add(item.getRoom());
		}

		return rooms;
	}

	public SortedSet<Room> findRoomsWithFavoriteSessions(Date date) {

		SortedSet<Room>rooms = new TreeSet<Room>();
		for (ScheduleItem item : findBreakoutItemsOnDate(date)) {
			if (item.isFavorite()) {
				rooms.add(item.getRoom());
			}
		}

		return rooms;
	}

	public List<ScheduleItem> findBreakoutItemsOnDateInRoom(Date date, Room room) {
		ArrayList<ScheduleItem> items = new ArrayList<ScheduleItem>();
		for (ScheduleItem item : findBreakoutItemsOnDate(date)) {
			if (item.getRoom().equals(room)) {
				items.add(item);
			}
		}
		return items;
	}

}
