package com.ddev.MessageApp.chat.controller;
import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.dto.MessageResponse;
import com.ddev.MessageApp.chat.model.FileType;
import com.ddev.MessageApp.chat.service.ChatService;
import com.ddev.MessageApp.user.service.UserService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {
    private final ChatService chatService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAudioFile(@RequestPart("file") MultipartFile file,
                                             @RequestPart("data") Message message) {
        try {
            // Upload to Firebase Storage
            Bucket bucket = StorageClient.getInstance().bucket();
            String uniqueFileName = UUID.randomUUID().toString();
            Blob blob = bucket.create(uniqueFileName, file.getBytes(), file.getContentType());


            String fileUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName() +"?alt=media";

            message.setFileType(FileType.AUDIO);
            message.setFileUrl(fileUrl);
            return ResponseEntity.ok().body(chatService.saveMessage(message));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir el archivo");
        }
    }
    @PostMapping(
            path = "/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProfilePicture(
            @RequestPart("file") MultipartFile file,
            @RequestParam("userId") Integer userId    // ← cambio aquí
    ) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String uniqueFileName = "profile_" + userId + "_" + UUID.randomUUID();

            Blob blob = bucket.create(uniqueFileName, file.getBytes(), file.getContentType());
            String fileUrl = "https://firebasestorage.googleapis.com/v0/b/"
                    + bucket.getName() + "/o/" + blob.getName() + "?alt=media";

            // Actualizas la URL en la base de datos del usuario
            userService.updateProfilePicture(userId, fileUrl);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir la foto de perfil");
        }
    }
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(@PathVariable Integer id) {
        userService.removeProfilePicture(id);
        return ResponseEntity.ok().build();
    }

}
