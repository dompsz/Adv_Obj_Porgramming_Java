package com.project.model;

import java.util.Set;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class Student {
	@Id
    private Integer studentId;

    @NotBlank(message = "Pole imię nie może być puste!")
    @Size(min = 2, max = 50, message = "Imię musi mieć od {min} do {max} znaków!")
    private String imie;

    @NotNull(message = "Pole nazwisko nie może być puste!")
    @Size(min = 2, max = 100, message = "Nazwisko musi mieć od {min} do {max} znaków!")
    private String nazwisko;

    @NotNull(message = "Pole nrIndeksu nie może być puste!")
    private String nrIndeksu;

    @NotBlank(message = "Email jest wymagany!")
    @Size(max = 50, message = "Email nie może mieć więcej niż {max} znaków!")
    private String email;

    private Boolean stacjonarny;

    @JsonIgnoreProperties({"studenci"})
    private Set<Projekt> projekty;
}
