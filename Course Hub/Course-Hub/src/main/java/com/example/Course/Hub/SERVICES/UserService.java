package com.example.Course.Hub.SERVICES;

import com.example.Course.Hub.ENTITY.Users;

public interface UserService {

    //Add new User to Database
    String adduser(Users user);


    //Check Email is Already present in data or not
    boolean checkEmail(String email);


    //Validate/check user email and password
    boolean valid(String email ,  String password );



    Users getUserByEmail(String email);


}
