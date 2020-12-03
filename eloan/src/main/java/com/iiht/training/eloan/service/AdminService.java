package com.iiht.training.eloan.service;

import java.util.List;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.LoanException;

public interface AdminService {

	
	public UserDto registerClerk(UserDto userDto)throws LoanException;
		
	public UserDto registerManager(UserDto userDto)throws LoanException;
	
	public List<UserDto> getAllClerks()throws LoanException;
		
	public List<UserDto> getAllManagers()throws LoanException;
	
	
}
