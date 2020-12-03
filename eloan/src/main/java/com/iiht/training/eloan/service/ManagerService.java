package com.iiht.training.eloan.service;

import java.util.List;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.exception.LoanException;

public interface ManagerService {
	
	public List<LoanOutputDto> allProcessedLoans() throws LoanException;
	
	public RejectDto rejectLoan(Long managerId,
								Long loanAppId,
								RejectDto rejectDto)throws LoanException;
	
	
	public SanctionOutputDto sanctionLoan(Long managerId,
										  Long loanAppId,
										  SanctionDto sanctionDto)throws LoanException;
}
