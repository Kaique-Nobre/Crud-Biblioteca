package com.example.SistemaBlblioteca.mapper;

import com.example.SistemaBlblioteca.dto.livroDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.livroDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "category", ignore = true)
    Book toEntity(BookRequestDTO livroDTO);

    @Mapping(source = "category.name", target = "category")
    BookResponseDTO toDTO(Book book);
}
