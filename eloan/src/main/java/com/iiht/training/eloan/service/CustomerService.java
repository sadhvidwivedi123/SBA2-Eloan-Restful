package com.iiht.training.eloan.service;

import java.util.List;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.LoanException;
import com.iiht.training.eloan.entity.Users;

public interface CustomerService {
	
	public UserDto register(UserDto userDto) throws LoanException;
	
	public LoanOutputDto applyLoan(Long customerId,
								   LoanDto loanDto) throws LoanException;
	
	public LoanOutputDto getStatus(Long loanAppId) throws LoanException;
		
	public List<LoanOutputDto> getStatusAll(Long customerId) throws LoanException;

	//public Users getUserById(Long userId)throws LoanException;
}
