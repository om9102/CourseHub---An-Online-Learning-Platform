package com.example.Course.Hub.SERVICES;


import com.example.Course.Hub.ENTITY.Users;
import com.example.Course.Hub.REPOSITORY.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository repo;


    /*// Either use AutoWired or use this constructor
    //Autowired create the object of repository and add them automatically
    public UserServiceImplementation(UserRepository repo) {
        this.repo = repo;
    }*/

    @Override
    public String adduser(Users user) {
        repo.save(user);
        return "User Added Successfully";
    }



    @Override
    public boolean checkEmail(String email) {
        return  repo.existsByEmail(email);
    }




    @Override
    public boolean valid(String email, String password) {

        if(repo.existsByEmail(email)){
          Users u =  repo.getByEmail(email);
          String dbPassword = u.getPassword();

          if(password.equals(dbPassword)){
              return true;
          } else{
              return false;
          }

        }

        return false;
    }



    @Override
    public Users getUserByEmail(String email) {
        return repo.getByEmail(email);
    }


}
