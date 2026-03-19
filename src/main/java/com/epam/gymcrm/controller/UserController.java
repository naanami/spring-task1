package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.ActivationRequest;
import com.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.epam.gymcrm.facade.UserFacade;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.UserService;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Api(tags = "Users")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final UserFacade userFacade;

    public UserController(AuthService authService, UserService userService, UserFacade userFacade) {
        this.authService = authService;
        this.userService = userService;
        this.userFacade = userFacade;
    }

    @GetMapping("/login")
    @ApiOperation("Authenticate user")
    public String login(
            @ApiParam(value = "Username", required = true)
            @RequestParam String username,
            @ApiParam(value = "Password", required = true)
            @RequestParam String password
    ) {
        authService.authenticate(username, password);
        return "Login successful";
    }

    @PutMapping("/password")
    @ApiOperation("Change user password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );
        return "Password changed successfully";
    }

    @PatchMapping("/{username}/activation")
    @ApiOperation("Activate or deactivate user")
    public String updateActivation(
            @ApiParam(value = "Username", required = true)
            @PathVariable String username,
            @ApiParam(value = "Password", required = true)
            @RequestParam String password,
            @Valid @RequestBody ActivationRequest request
    ) {
        if (Boolean.TRUE.equals(request.getIsActive())) {
            userFacade.activateUser(username, password);
            return "User activated";
        }

        userFacade.deactivateUser(username, password);
        return "User deactivated";
    }
}