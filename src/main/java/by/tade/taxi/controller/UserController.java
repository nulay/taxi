package by.tade.taxi.controller;

import by.tade.taxi.dto.DiscountGasDto;
import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;
import by.tade.taxi.entity.UserTaxiEntity;
import by.tade.taxi.entity.repository.UserRepository;
import by.tade.taxi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public String login(@ModelAttribute("userLogin") UserLoginDto userLogin, Model model) {
//        String page = userService.login(userLogin) == true ? "redirect:/balance" : "index";

        userService.login(userLogin);
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String loginPageView() {
        return "start";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userLogin") RegistrationDto registration) {
        return userService.registration(registration) == true ? "index" : "index";
    }

    @GetMapping("/registration-page")
    public String registrationPageView() {
        return "registration";
    }

    @PostMapping("/user-settings")
    public String userSettings(@ModelAttribute("userSettings") UserSettingsDto userSettings) {

        return userService.saveUserSettings(userSettings) ? "redirect:/index" : "error";
    }

    @GetMapping("/user-settings-page")
    public String userSettingsPageView(Model model) {
        UserSettingsDto userSettings = userService.getUserSettings();

        while(userSettings.getDiscountGas().size() < 10){
            userSettings.getDiscountGas().add(new DiscountGasDto(BigDecimal.ZERO, BigDecimal.ZERO));
        }
        model.addAttribute("userSettings", userSettings);
        return "userSettingsPage";
    }

    @GetMapping("/logout")
    public String logout() {
        UserSessionDto userSession = userService.getUserSession();
        userSession.clean();
        return "start";
    }
}
