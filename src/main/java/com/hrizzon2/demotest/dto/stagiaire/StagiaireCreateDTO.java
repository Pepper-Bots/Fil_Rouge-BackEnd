package com.hrizzon2.demotest.dto.stagiaire;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StagiaireCreateDTO {

    @NotBlank
    private String lastName;

    @NotBlank
    private String firstName;

    @Email
    private String email;

    @Pattern(regexp = "^(\\+33|0)[1-9](\\d{2}){4}$")
    private Number phone;

    private Long villeId;

    private List<Long> dossiersIds;
}
