package com.iiht.training.eloan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.LoanException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ManagerService;
import com.iiht.training.eloan.util.LoanParser;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public List<LoanOutputDto> allProcessedLoans()throws LoanException {
		
		List<LoanOutputDto> loanOutputdtos=new ArrayList<LoanOutputDto>();
		LoanOutputDto loanOutputTemp=new LoanOutputDto();
		UserDto userDtoTemp;
		ProcessingDto processDtoTemp;
		List<LoanDto> loanDtos=loanRepository.findAllByStatus(LoanParser.convert("processed")).stream().map(e->{
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
			if(LoanParser.convert(loanDtos.get(i).getStatus())==1)
			{
				userDtoTemp=LoanParser.parse(usersRepository.findById(loanDtos.get(i).getUserId()).get());
				loanDtos.get(i).setUser(userDtoTemp);
				processDtoTemp=LoanParser.parse((pProcessingInfoRepository.findByProcessLoan(LoanParser.parse(loanDtos.get(i)))));
				loanOutputTemp=new LoanOutputDto(loanDtos.get(i).getUserId(), loanDtos.get(i).getLoanId(), userDtoTemp, loanDtos.get(i), processDtoTemp, null, loanDtos.get(i).getStatus(), loanDtos.get(i).getRemark());
			}
			loanOutputdtos.add(loanOutputTemp);
		}
				return loanOutputdtos;
	}

	@Override
	public RejectDto rejectLoan(Long managerId, Long loanAppId, RejectDto rejectDto) throws LoanException {
		
		RejectDto rejectDtto=new RejectDto();
		
		if(!usersRepository.existsById(managerId))
		{
			throw new LoanException(managerId+" Manager id doesnot exist");
		}
		else if(!LoanParser.parse(usersRepository.findById(managerId).get()).getRole().trim().equalsIgnoreCase("manager"))
		{
			throw new LoanException("Invalid user role for the operation");
		}
		if(!loanRepository.existsById(loanAppId))
		{
			throw new LoanException(loanAppId+" Loan id does not exist");
		}
		if(!LoanParser.parse((loanRepository.findById(loanAppId).get())).getStatus().trim().equalsIgnoreCase("processed"))
		{
			throw new LoanException("Only Processed loans can be rejected");
		}
		
		LoanDto loanDto=LoanParser.parse(loanRepository.findById(loanAppId).get());
		loanDto.setUser(LoanParser.parse(usersRepository.findById(loanDto.getUserId()).get()));
		loanDto.setStatus("rejected");
		loanDto.setRemark(rejectDto.getRemark());
		loanRepository.save(LoanParser.parse(loanDto));
		rejectDtto.setRemark(rejectDto.getRemark());
		return rejectDtto;
	}

	@Override
	public SanctionOutputDto sanctionLoan(Long managerId, Long loanAppId, SanctionDto sanctionDto) throws LoanException {
		SanctionDto sDto=new SanctionDto();
		SanctionOutputDto soDto=new SanctionOutputDto();
		
		if(!usersRepository.existsById(managerId))
		{
			throw new LoanException(managerId+" Manager id doesnot exist");
		}
		else if(!LoanParser.parse(usersRepository.findById(managerId).get()).getRole().trim().equalsIgnoreCase("manager"))
		{
			throw new LoanException("Invalid user role for the operation");
		}
		if(!loanRepository.existsById(loanAppId))
		{
			throw new LoanException(loanAppId+" Loan id does not exist");
		}
		if(!LoanParser.parse((loanRepository.findById(loanAppId).get())).getStatus().trim().equalsIgnoreCase("processed"))
		{
			throw new LoanException(" Only Processed loans can be sanctioned");
		}
		
		LoanDto loanDto=LoanParser.parse(loanRepository.findById(loanAppId).get());
		loanDto.setUser(LoanParser.parse(usersRepository.findById(loanDto.getUserId()).get()));
		loanDto.setStatus("sanctioned");
		loanDto.setRemark(sanctionDto.getRemark());
		loanRepository.save(LoanParser.parse(loanDto));
		
		
		sDto.setLoanId(loanAppId);
		sDto.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());		
		sDto.setPaymentStartDate(sanctionDto.getPaymentStartDate());
		sDto.setTermOfLoan(sanctionDto.getTermOfLoan());
		sDto.setManagerId(managerId);
		sDto.setLoanClosureDate(sanctionDto.getPaymentStartDate().plusYears(Math.round(sanctionDto.getTermOfLoan())));		
		sDto.setMonthlyPayment(Math.pow((sanctionDto.getLoanAmountSanctioned()*(1+12/100)), sanctionDto.getTermOfLoan()));
		sDto.setRemark(sanctionDto.getRemark());
		sDto.setsLoan(loanDto);
		
		soDto.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
		soDto.setLoanClosureDate(sanctionDto.getPaymentStartDate().plusYears(Math.round(sanctionDto.getTermOfLoan())));
		soDto.setLoanId(loanAppId);
		soDto.setMonthlyPayment(Math.pow((sanctionDto.getLoanAmountSanctioned()*(1+12/100)), sanctionDto.getTermOfLoan()));
		soDto.setPaymentStartDate(sanctionDto.getPaymentStartDate());
		soDto.setTermOfLoan(sanctionDto.getTermOfLoan());
		
		
		sanctionInfoRepository.save(LoanParser.parse(sDto));
		//sanctionInfoRepository.updateCalculatedValues(loanAppId);
		 return soDto;
		
	}

}
