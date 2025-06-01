package com.github.GCrel.web.controller.vo;

import com.github.GCrel.web.models.UserDTO;

public record UpdateRequest(
        UserDTO user,
        String jwtToken
) {
}
