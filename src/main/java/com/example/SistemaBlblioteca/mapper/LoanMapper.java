package com.example.SistemaBlblioteca.mapper;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanResponseDTO;
import com.example.SistemaBlblioteca.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "book.title", target = "book")
    @Mapping(source = "user.email", target = "user")
    @Mapping(source = "loanDate", target = "loanDate")
    LoanResponseDTO toDTO(Loan loan);

    List<LoanResponseDTO> toLoanResponseList(List<Loan> loans);

    default LocalDate map(LocalDateTime value) {

        if (value == null) {
            return null;
        }

        return value.toLocalDate();
    }
}
