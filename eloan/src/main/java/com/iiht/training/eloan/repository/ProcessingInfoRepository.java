package com.iiht.training.eloan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;

@Repository
public interface ProcessingInfoRepository extends JpaRepository<ProcessingInfo, Long>{

	ProcessingInfo findByProcessLoan(Loan loan);

	//ProcessingInfo findByLoanId(Loan loan);
}
