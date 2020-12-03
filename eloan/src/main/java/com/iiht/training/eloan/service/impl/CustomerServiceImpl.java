package com.iiht.training.eloan.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.LoanException;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.CustomerService;
import com.iiht.training.eloan.util.LoanParser;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository processingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Transactional
	@Override
	public UserDto register(UserDto userDto) throws LoanException {
		if(userDto!=null) {
			if(!userDto.getRole().trim().equals("customer")) {
				throw new LoanException("Error registering, given role is incorrect. Expected- customer");
			}
			
			userDto=LoanParser.parse(usersRepository.save(LoanParser.parse(userDto)));
		}
		return userDto;
	}

	@Transactional
	@Override
	public LoanOutputDto applyLoan(Long customerId, LoanDto loanDto) throws LoanException {
		
		LoanOutputDto loanOutputDto=null;
		
		if(loanDto!=null) {
		if(!usersRepository.existsById(customerId)) {
			throw new LoanException("Given customer Id does not exist, please register as customer for applying loan");
		}
		loanDto.setLoanApplicationDate(LocalDate.now());
		loanDto.setStatus("applied");		 
		UserDto userDto = LoanParser.parse(usersRepository.findById(customerId).get());
		loanDto.setUser(userDto);
		loanDto=LoanParser.parse(loanRepository.save(LoanParser.parse(loanDto)));
		loanOutputDto = new LoanOutputDto(userDto.getUserId(),loanDto.getLoanId(),userDto, loanDto,loanDto.getStatus());
				
	}
		return loanOutputDto;
	}

	@Override
	public LoanOutputDto getStatus(Long loanAppId) throws LoanException {
		
		LoanOutputDto loanOutputDto=null;
		LoanDto loanDto = new LoanDto();
		ProcessingDto processingDto = new ProcessingDto();
		SanctionOutputDto sanctionOutputDto = new SanctionOutputDto();
		UserDto userDto = new UserDto();
		
		if(!loanRepository.existsById(loanAppId)) {
			
			throw new LoanException("Loan Application Id given does not exist!");
		}
		loanDto=LoanParser.parse(loanRepository.findById(loanAppId).get());
		userDto = LoanParser.parse(usersRepository.findById(loanDto.getUserId()).get());
		if(loanDto.getStatus().trim().equalsIgnoreCase("Processed")||loanDto.getStatus().trim().equalsIgnoreCase("Sanctioned")||loanDto.getStatus().trim().equalsIgnoreCase("Rejected"))
			processingDto = LoanParser.parse(processingInfoRepository.findByProcessLoan(LoanParser.parse(loanDto)));
		else
			processingDto=null;
		if(loanDto.getStatus().trim().equalsIgnoreCase("Sanctioned"))
			sanctionOutputDto = LoanParser.parse1(sanctionInfoRepository.findBySanctionLoan(LoanParser.parse(loanDto)));
		else
			sanctionOutputDto=null;
		
		loanOutputDto = new LoanOutputDto(userDto.getUserId(), loanAppId, userDto, loanDto, processingDto, sanctionOutputDto,loanDto.getStatus(), loanDto.getRemark());
		
		return loanOutputDto;
	}

	@Override
	public List<LoanOutputDto> getStatusAll(Long customerId) throws LoanException{
		
		List<LoanOutputDto> loanOutputDtos = new ArrayList<LoanOutputDto>();
		List<ProcessingDto> processingDtos =  new ArrayList<ProcessingDto>();
		List<SanctionOutputDto> sanctionOutputDtos = new ArrayList<SanctionOutputDto>();
		UserDto userDto = new UserDto();
		
		if(!usersRepository.existsById(customerId)) {
			
			throw new LoanException("Customer does not exist!");
		}
		userDto = LoanParser.parse(usersRepository.findById(customerId).get());
		
		List<LoanDto> loanDtos=loanRepository.findAllByUser(LoanParser.parse(userDto)).stream().map(e->{
			try {
				return LoanParser.parse(e);
			} catch (LoanException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		
		}).collect(Collectors.toList());
		
		for(int i=0;i<loanDtos.size();i++) {
			
			if(loanDtos.get(i).getStatus().trim().equalsIgnoreCase("Processed")||loanDtos.get(i).getStatus().trim().equalsIgnoreCase("Sanctioned")||loanDtos.get(i).getStatus().trim().equalsIgnoreCase("Rejected"))
				processingDtos.add(i, LoanParser.parse(processingInfoRepository.findByProcessLoan(LoanParser.parse(loanDtos.get(i)))));
			else
				processingDtos.add(i,null);
			
		if(loanDtos.get(i).getStatus().trim().equalsIgnoreCase("Sanctioned"))
			sanctionOutputDtos.add(i, LoanParser.parse1(sanctionInfoRepository.findBySanctionLoan(LoanParser.parse(loanDtos.get(i))))); 
		else
			sanctionOutputDtos.add(i,null);
		
		
		loanOutputDtos.add(i, new LoanOutputDto(userDto.getUserId(), loanDtos.get(i).getLoanId(), userDto, loanDtos.get(i), processingDtos.get(i), sanctionOutputDtos.get(i),loanDtos.get(i).getStatus(), loanDtos.get(i).getRemark()));
		
		}
		return loanOutputDtos;
	}
	
}
