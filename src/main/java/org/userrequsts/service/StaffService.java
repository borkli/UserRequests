package org.userrequsts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.userrequsts.dto.RequestFilter;
import org.userrequsts.dto.UserFilter;
import org.userrequsts.model.RequestStatus;
import org.userrequsts.model.Role;
import org.userrequsts.model.User;
import org.userrequsts.model.UserException;
import org.userrequsts.model.UserRequest;
import org.userrequsts.repository.RequestRepository;
import org.userrequsts.repository.UserRepository;

import java.util.List;
import java.util.StringJoiner;

@Service
public class StaffService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    public List<UserRequest> getRequests(RequestFilter filter) {
        RequestStatus status = RequestStatus.SENT;
        Sort sort = UserService.getSort(filter);
        Pageable pageable = PageRequest.of(filter.getOffset(), filter.getLimit(), sort);
        List<UserRequest> result;
        if (filter.getUserName() == null || filter.getUserName().isBlank()) {
            result = requestRepository.getRequestsByStatus(status, pageable);
        } else {
            result = requestRepository.getRequestsByStatus(
                status, filter.getUserName() + "%", pageable
            );
        }
        if (result.isEmpty()) {
            return result;
        }
        modifyMessage(result);
        return result;
    }

    private void modifyMessage(List<UserRequest> requests) {
        for (UserRequest request : requests) {
            String message = request.getMessage();
            if (message.length() < 2) {
                continue;
            }
            StringJoiner joiner = new StringJoiner("-");
            for (char c : message.toCharArray()) {
                joiner.add(String.valueOf(c));
            }
            request.setMessage(joiner.toString());
        }
    }

    public boolean updateRequestStatus(long requestId, RequestStatus status) {
        try {
            UserRequest request = requestRepository
                .findById(requestId)
                .orElseThrow();
            if (status != RequestStatus.ACCEPTED &&
                status != RequestStatus.REJECTED) {
                throw new UserException("invalid_status");
            }
            requestRepository.save(
                request.setStatus(status)
            );
            return true;
        } catch (Exception ex) {
            throw new UserException("request_not_found");
        }
    }

    public List<User> getUsers(UserFilter filter) {
        if (filter.getUserName() == null ||
            filter.getUserName().isBlank()) {
            return userRepository.findAll();
        }
        return userRepository.getUsersByName(filter.getUserName() + "%");
    }

    public boolean addRole(long userId) {
        User user = getUser(userId);
        if (!user.getRoles().contains(Role.OPERATOR)) {
            user.getRoles().add(Role.OPERATOR);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private User getUser(long userId) {
        try {
            return userRepository.findById(userId)
                .orElseThrow();
        } catch (Exception ex) {
            throw new UserException("user_not_found");
        }
    }
}
