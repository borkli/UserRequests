package org.userrequsts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.userrequsts.dto.RequestFilter;
import org.userrequsts.dto.UserFilter;
import org.userrequsts.model.RequestStatus;
import org.userrequsts.model.User;
import org.userrequsts.model.UserRequest;
import org.userrequsts.service.StaffService;

import java.util.List;

@RestController
@RequestMapping("/staff/")
public class StaffController extends CommonController {

    @Autowired
    private StaffService staffService;

    @PreAuthorize("hasAuthority('OPERATOR')")
    @GetMapping("getRequests")
    public List<UserRequest> getRequests(RequestFilter filter) {
        return staffService.getRequests(filter);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping("updateRequestStatus")
    public boolean updateRequestStatus(@RequestParam long requestId,
                                       RequestStatus status) {
        return staffService.updateRequestStatus(requestId, status);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("getUsers")
    public List<User> getUsers(UserFilter filter) {
        return staffService.getUsers(filter);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("addRole")
    public boolean addRole(@RequestParam long userId) {
        return staffService.addRole(userId);
    }
}
