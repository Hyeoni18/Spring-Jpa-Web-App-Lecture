package hello.springjpa.webapp.settings;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//bean 등록 필요 없음, 다른 bean을 사용할 필요가 없어서 그냥 new해서 쓰면 돼.
public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.value","입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}
