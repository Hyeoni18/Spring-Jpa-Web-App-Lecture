package hello.springjpa.webapp.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component // 2. 똑같이 bean으로 만들어줘야 해. spring은 bean과 bean만 의존성 주입받을 수 있음. 아니면 명시적으로 넣어줘야 해.
@RequiredArgsConstructor // lombok이 제공하는 어노테이션. final 타입 멤버 변수의 생성자를 만들어 줌. 선별적으로 반드시 필요한 생성자를 고를 수 있음.
public class SignUpFormValidator implements Validator {
    // 커스텀 검증 (중복 이메일, 닉네임 여부 확인)
    private final AccountRepository accountRepository; // 1. repository가 bean이니까

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class); // SignUpForm 타입의 인스턴스를 검증
    }

    @Override
    public void validate(Object target, Errors errors) {
        // TODO email, nickname
        SignUpForm signUpForm = (SignUpForm) target;
        if(accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일 입니다.");
        }
        
        if(accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임 입니다.");
        }
    }
}
