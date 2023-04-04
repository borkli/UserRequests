package org.userrequsts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.userrequsts.dto.RequestFilter;
import org.userrequsts.dto.SortBy;
import org.userrequsts.dto.UserRequestDto;
import org.userrequsts.model.RequestStatus;
import org.userrequsts.model.User;
import org.userrequsts.model.UserException;
import org.userrequsts.model.UserRequest;
import org.userrequsts.repository.RequestRepository;
import org.userrequsts.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    public Long createOrUpdateRequest(long userId, UserRequestDto request) {
        User user = getUser(userId);
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new UserException("empty_message");
        }
        UserRequest existRequest;
        if (request.getId() != null) {
            try {
                existRequest = requestRepository.findById(request.getId())
                    .orElseThrow();
                requestRepository.save(
                    existRequest.setMessage(request.getMessage())
                );
            } catch (Exception ex) {
                throw new UserException("request_not_found");
            }
        } else {
            if (request.getStatus() == null || request.getStatus() != RequestStatus.DRAFT) {
                request.setStatus(RequestStatus.SENT);
            }
            existRequest = requestRepository.saveAndFlush(
                new UserRequest()
                    .setUser(user)
                    .setMessage(request.getMessage())
                    .setStatus(request.getStatus())
                    .setCreatedDate(LocalDateTime.now())
            );
        }
        return existRequest.getId();
    }

    public List<UserRequest> getRequests(long userId, RequestFilter filter) {
        return requestRepository.getRequestsByUser(
            userId, PageRequest.of(
                filter.getOffset(), filter.getLimit(), getSort(filter)
            )
        );
    }

    public UserRequest getActiveRequest(long userId) {
        List<UserRequest> result = requestRepository.getActiveRequest(
            userId, RequestStatus.DRAFT,
            PageRequest.of(
                0, 1, Sort.by("createdDate").descending()
            )
        );
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    private User getUser(long userId) {
        try {
            return userRepository.findById(userId)
                .orElseThrow();
        } catch (Exception ex) {
            throw new UserException("user_not_found");
        }
    }

    public static Sort getSort(RequestFilter filter) {
        Sort sortField = Sort.by("createdDate");
        SortBy sortBy = filter.getSortBy();
        return sortBy == null || sortBy == SortBy.ASC ?
            sortField.ascending() : sortField.descending();
    }
}
