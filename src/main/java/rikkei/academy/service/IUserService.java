package rikkei.academy.service;

import rikkei.academy.model.dto.request.FormLogin;
import rikkei.academy.model.dto.request.FormRegister;
import rikkei.academy.model.dto.response.JWTResponse;

public interface IUserService {
    boolean register(FormRegister formRegister);

    JWTResponse login(FormLogin formLogin);
}
