package org.example.campconnect.Service;

import org.example.campconnect.dto.ResponseCreateRequest;
import org.example.campconnect.dto.ResponseDTO;
import org.example.campconnect.dto.ResponseUpdateRequest;

import java.util.List;

public interface IResponseService {

    ResponseDTO addResponse(ResponseCreateRequest request, String userEmail);

    ResponseDTO getResponseById(Long id);

    List<ResponseDTO> getResponsesByComment(Long commentId);

    ResponseDTO updateResponse(Long id, ResponseUpdateRequest request, String userEmail);

    void deleteResponse(Long id, String userEmail);
}