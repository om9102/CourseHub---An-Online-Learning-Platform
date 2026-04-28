package com.example.Course.Hub.Controller;

import com.example.Course.Hub.ENTITY.Users;
import com.example.Course.Hub.SERVICES.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ServicesController {

    @Autowired
    UserService uService;


    /*@PostMapping("/adduser")
    public String addUser( @RequestParam("name") String name,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirmpassword") String confirmPassword,
                           @RequestParam("role") String role,
                           @RequestParam("phone")   String phone){

        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmpassword(confirmPassword);
        user.setRole(role);
        user.setPhone(phone);

        uService.adduser(user);

        return "index";
    }*/


    @PostMapping("/adduser")
    public String addUser(@ModelAttribute Users user){

        boolean emailexists = uService.checkEmail(user.getEmail());

        if(emailexists == false){
            uService.adduser(user);
            System.out.println("User registered successfully");
            return "Authenticate/index";
        } else {
            System.out.println("Email already exists");
            return "/signup";
        }

    }





    /*@PostMapping("validate")
    public String validateUser(@RequestParam("email")  String email ,
                               @RequestParam("password") String password

    ){

        if (uService.checkEmail(email)) {


            boolean val = uService.valid(email, password);

            if (val == true) {
                System.out.println("Login Successfull");
                return "/home";
            } else {
                System.out.println("Invalid Credentials. Try Again!");
                return "/login";
            }

        } else{
                return "/login";
            }

        }
        */




    @PostMapping("validate")
    public String validateUser(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session) {
        if (uService.checkEmail(email)) {
            boolean val = uService.valid(email, password);
            if (val) {
                Users user = uService.getUserByEmail(email); // add this method
                session.setAttribute("user", user);
                // Route by role
                if ("Trainer".equalsIgnoreCase(user.getRole())) {
                    return "redirect:/trainer-home";
                } else {
                    return "redirect:/student-home";
                }
            }
        }
        return "redirect:/login";
    }










}
