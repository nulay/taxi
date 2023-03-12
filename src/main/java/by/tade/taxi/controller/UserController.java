package by.tade.taxi.controller;

import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.http.HttpRequest;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;


    @PostMapping("/login")
    public String transaction(@ModelAttribute("userLogin") UserLoginDto userLogin) {
        return userService.login(userLogin) == true ? "redirect:/balance" : "login";
    }

    @GetMapping("/login-page")
    public String loginPageView(Model model) {
        model.addAttribute("userLogin", new UserLoginDto());
        return "login";
    }
}
