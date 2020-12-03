package com.iiht.training.eloan.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserDto registerClerk(UserDto userDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDto registerManager(UserDto userDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserDto> getAllClerks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserDto> getAllManagers() {
		// TODO Auto-generated method stub
		return null;
	}

}
