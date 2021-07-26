package top.tangyh.basic.exception;


import top.tangyh.basic.exception.code.ExceptionCode;

/**
 * 无权限异常
 *
 * @author zuihou
 * @version 1.0
 */
public class UnauthorizedException extends BaseUncheckedException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(int code, String message) {
        super(code, message);
    }

    public static UnauthorizedException wrap(String msg) {
        return new UnauthorizedException(ExceptionCode.FORBIDDEN.getCode(), msg);
    }

    @Override
    public String toString() {
        return "UnauthorizedException [message=" + getMessage() + ", code=" + getCode() + "]";
    }

}
