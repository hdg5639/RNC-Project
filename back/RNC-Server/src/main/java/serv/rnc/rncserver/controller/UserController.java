package serv.rnc.rncserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import serv.rnc.rncserver.dto.user.UserDTO;
import serv.rnc.rncserver.dto.user.UserDetailDTO;
import serv.rnc.rncserver.serivce.impl.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody UserDetailDTO userDetailDTO) {
        userService.signup(userDetailDTO);
    }

    @PostMapping("signin")
    public void signin(@RequestBody UserDTO userDTO) {}
}
