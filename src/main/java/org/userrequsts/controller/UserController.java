package org.userrequsts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.userrequsts.dto.RequestFilter;
import org.userrequsts.dto.UserRequestDto;
import org.userrequsts.model.UserRequest;
import org.userrequsts.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user/")
@PreAuthorize("hasAuthority('USER')")
public class UserController extends CommonController {

    @Autowired
    private UserService userService;

    @PostMapping("createOrUpdateRequest")
    public Long createOrUpdateRequest(@RequestParam long userId,
                                      @RequestBody UserRequestDto request) {
        return userService.createOrUpdateRequest(userId, request);
    }

    @GetMapping("getRequests")
    public List<UserRequest> getRequests(@RequestParam long userId,
                                         RequestFilter filter) {
        return userService.getRequests(userId, filter);
    }

    @GetMapping("getActiveRequest")
    public UserRequest getActiveRequest(@RequestParam long userId) {
        return userService.getActiveRequest(userId);
    }
}
