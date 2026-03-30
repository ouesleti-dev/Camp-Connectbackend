package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Comment;
import org.example.campconnect.Entity.Response;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.CommentRepository;
import org.example.campconnect.Repository.ResponseRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ResponseCreateRequest;
import org.example.campconnect.dto.ResponseDTO;
import org.example.campconnect.dto.ResponseUpdateRequest;
import org.example.campconnect.mapper.ResponseMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IResponseServiceImp implements IResponseService {

    private final ResponseRepository responseRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ResponseMapper responseMapper;

    @Override
    public ResponseDTO addResponse(ResponseCreateRequest request, String userEmail) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Commentaire introuvable avec l'ID : " + request.getCommentId()));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userEmail));

        Response response = new Response();
        response.setContent(request.getContent());
        response.setCreateDate(LocalDate.now());
        response.setComment(comment);
        response.setUser(user);

        return responseMapper.toDto(responseRepository.save(response));
    }

    @Override
    public ResponseDTO getResponseById(Long id) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réponse introuvable avec l'ID : " + id));
        return responseMapper.toDto(response);
    }

    @Override
    public List<ResponseDTO> getResponsesByComment(Long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new IllegalArgumentException("Commentaire introuvable avec l'ID : " + commentId);
        return responseRepository.findByComment_IdOrderByCreateDateAsc(commentId)
                .stream()
                .map(responseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseDTO updateResponse(Long id, ResponseUpdateRequest request, String userEmail) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réponse introuvable avec l'ID : " + id));

        if (!response.getUser().getEmail().equals(userEmail))
            throw new SecurityException("Vous n'êtes pas autorisé à modifier cette réponse");

        response.setContent(request.getContent());
        return responseMapper.toDto(responseRepository.save(response));
    }

    @Override
    public void deleteResponse(Long id, String userEmail) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réponse introuvable avec l'ID : " + id));

        if (!response.getUser().getEmail().equals(userEmail))
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer cette réponse");

        responseRepository.deleteById(id);
    }
}