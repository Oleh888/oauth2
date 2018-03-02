package ua.yaroslav.auth2.auth.token;

import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.store.InMemoryStore;

@Component
public class Generator {
    private final Validator validator;
    private final InMemoryStore store;


    public Generator(Validator validator, InMemoryStore store) {
        this.validator = validator;
        this.store = store;
    }

    public String createCodeAndGetURL(AuthRequestDto authRequest){
        if(validator.validate(authRequest)){
            AuthCode code = JSONUtil.getCode(authRequest);
            store.addCode(code);
            return authRequest.getRedirectURI() + "?" +
                    "code=" + JSONUtil.encodeObject(code) + "&" + "state=awesome";
        }
        return null;
    }

    public Validator getValidator() {
        return validator;
    }
}