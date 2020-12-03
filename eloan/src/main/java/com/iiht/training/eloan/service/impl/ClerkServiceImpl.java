package com.iiht.training.eloan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.LoanException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ClerkService;
import com.iiht.training.eloan.util.LoanParser;

@Service
public class ClerkServiceImpl implements ClerkService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository processingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public List<LoanOutputDto> allAppliedLoans() throws LoanException {
		List<LoanOutputDto> loanOutputdtos=new ArrayList<LoanOutputDto>();
		LoanOutputDto loanOutputTemp=new LoanOutputDto();
		UserDto userDtoTemp;
		List<LoanDto> loanDtos=loanRepository.findAllByStatus(LoanParser.convert("applied")).stream().map(e->{
			try {
				return LoanParser.parse(e);
			} catch (LoanException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
		
		
		for(int i=0;i<loanDtos.size();i++)
		{
			if(LoanParser.convert(loanDtos.get(i).getStatus())==0)
			{
				userDtoTemp=LoanParser.parse(usersRepository.findById(loanDtos.get(i).getUserId()).get());
				loanOutputTemp=new LoanOutputDto(loanDtos.get(i).getUserId(), loanDtos.get(i).getLoanId(), userDtoTemp, loanDtos.get(i), null, null, loanDtos.get(i).getStatus(), loanDtos.get(i).getRemark());
			}
			loanOutputdtos.add(loanOutputTemp);
		}
				return loanOutputdtos;
	}

	@Override
	public ProcessingDto processLoan(Long clerkId, Long loanAppId, ProcessingDto processingDto) throws LoanException {
		ProcessingDto processDtoTemp=new ProcessingDto();
		
		if(!usersRepository.existsById(clerkId))
		{
			throw new LoanException(clerkId+" Clerk id doesnot exist");
		}
		else if(!LoanParser.parse(usersRepository.findById(clerkId).get()).getRole().trim().equalsIgnoreCase("clerk"))
		{
			throw new LoanException("Invalid user role for the operation");
		}
		if(!loanRepository.existsById(loanAppId))
		{
			throw new LoanException(loanAppId+" Loan id does not exist");
		}
		if(!LoanParser.parse((loanRepository.findById(loanAppId).get())).getStatus().trim().equalsIgnoreCase("applied"))
		{
			throw new LoanException(" Loan status other than applied cannot be processed");
		}
		LoanDto loanDto=LoanParser.parse(loanRepository.findById(loanAppId).get());
		loanDto.setUser(LoanParser.parse(usersRepository.findById(loanDto.getUserId()).get()));
		loanDto.setStatus("processed");
		
		processDtoTemp.setLoanId(loanAppId);
		processDtoTemp.setLoanDto(loanDto);
		processDtoTemp.setLoanClerkId(clerkId);
		processDtoTemp.setAcresOfLand(processingDto.getAcresOfLand());
		processDtoTemp.setAddressOfProperty(processingDto.getAddressOfProperty());
		processDtoTemp.setAppraisedBy(processingDto.getAppraisedBy());
		processDtoTemp.setLandValue(processingDto.getLandValue());
		processDtoTemp.setSuggestedAmountOfLoan(processingDto.getSuggestedAmountOfLoan());
		processDtoTemp.setValuationDate(processingDto.getValuationDate());
			
		processingInfoRepository.save((LoanParser.parse(processDtoTemp)));
		loanRepository.save(LoanParser.parse(loanDto));
		
		return processDtoTemp;
	} 

}
