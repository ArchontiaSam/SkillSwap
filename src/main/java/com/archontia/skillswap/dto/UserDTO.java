package com.archontia.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private Double rating;
    private String password;

    public UserDTO(Long id, String name, String email, Double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rating = rating;
    }
}
