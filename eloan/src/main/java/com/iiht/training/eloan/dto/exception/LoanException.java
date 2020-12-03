package com.iiht.training.eloan.dto.exception;

import org.springframework.validation.ObjectError;
import java.util.List;


public class LoanException extends Exception{
	
	public LoanException(String msg) {
		super(msg);
	}
	public static void toErrExceptions(List<ObjectError> allErrors) throws LoanException
	{
		StringBuilder sb=new StringBuilder();
		for(ObjectError err:allErrors)
		{
			sb.append(err.getDefaultMessage()+",");
		}
		String s=sb.toString();
		throw new LoanException(s);
	}
}
