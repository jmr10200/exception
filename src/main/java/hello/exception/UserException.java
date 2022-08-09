package hello.exception;

public class UserException extends RuntimeException {

    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

/* API 예외처리 - HandlerExceptionResolver 활용 */
// 예외를 여기서 마무리 하기
// 예외가 발생하면 WAS 까지 예외가 던져지고 WAS 에서 에러 페이지 정보를 찾아서 다시 /error 를 호출 하는 과정은 번거롭다.
// ExceptionResolver 를 활용하면 예외가 발생했을 때 이런 복잡한 과정 없이 여기에서 문제를 깔끔하게 해결할 수 있다.

