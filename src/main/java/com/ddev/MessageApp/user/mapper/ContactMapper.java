package com.ddev.MessageApp.user.mapper;

import com.ddev.MessageApp.user.dto.UserDTO;
import com.ddev.MessageApp.user.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);
    UserDTO userToDTO(UserEntity user);
}
