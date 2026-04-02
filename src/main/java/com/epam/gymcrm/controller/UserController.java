package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.epam.gymcrm.facade.UserFacade;
import com.epam.gymcrm.security.SecurityAccessService;
import com.epam.gymcrm.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Api(tags = "Users")
public class UserController {

    private final UserService userService;
    private final UserFacade userFacade;
    private final SecurityAccessService securityAccessService;

    public UserController(UserService userService,
                          UserFacade userFacade,
                          SecurityAccessService securityAccessService) {
        this.userService = userService;
        this.userFacade = userFacade;
        this.securityAccessService = securityAccessService;
    }

    @PutMapping("/password")
    @ApiOperation("Change user password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        securityAccessService.ensureSameUser(request.getUsername());

        userService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );
        return "Password changed successfully";
    }

    @PatchMapping("/{username}/activation")
    public String toggleActivation(@PathVariable String username) {
        securityAccessService.ensureSameUser(username);
        userFacade.toggleUserActivation(username);
        return "User activation status changed successfully";
    }
}