package com.mAdmin.service;

import javax.servlet.http.HttpSession;

import com.mAdmin.entity.AttendeeInSession;


public interface RegisterService {

	
	void assignPotentialAttendeeToReservedAttendee(HttpSession session);

	
	void deletePotentialAttendee(AttendeeInSession potentialAttendee);
}