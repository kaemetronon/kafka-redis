package com.example.demo.service.request;

import com.example.demo.model.dto.UpdatedResultDto;
public interface IRequestService<T> {

    UpdatedResultDto doRequest(Class<T> clazz);
}
