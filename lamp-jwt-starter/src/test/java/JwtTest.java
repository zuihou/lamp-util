import com.tangyh.basic.jwt.JwtProperties;
import com.tangyh.basic.jwt.TokenUtil;
import com.tangyh.basic.jwt.model.AuthInfo;
import com.tangyh.basic.jwt.model.JwtUserInfo;

public class JwtTest {

    public static void main(String[] args) {
        JwtProperties properties = new JwtProperties();
        properties.setExpire(100L);
        properties.setRefreshExpire(7200L);

        AuthInfo build = build(properties);
        String token = build.getToken();
//        String token = "eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoi5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MOWnk-WQjemVv-W6puS4jeiDvei2hei_hzUw5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MOWnk-WQjemVv-W6puS4jeiDvei2hei_hzUw5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MOWnk-WQjemVv-W6puS4jeiDvei2hei_hzUw5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MCIsInRva2VuX3R5cGUiOiJ0b2tlbiIsInVzZXJpZCI6IjkyMjMzNzIwMzY4NTQ3NzU4MDciLCJhY2NvdW50Ijoi5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MOWnk-WQjemVv-W6puS4jeiDvei2hei_hzUw5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MOWnk-WQjemVv-W6puS4jeiDvei2hei_hzUw5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MOWnk-WQjemVv-W6puS4jeiDvei2hei_hzUw5aeT5ZCN6ZW_5bqm5LiN6IO96LaF6L-HNTDlp5PlkI3plb_luqbkuI3og73otoXov4c1MCIsImV4cCI6MTU4NTg0MTQ0NiwibmJmIjoxNTg1ODQxNDM2fQ.E4LNVX47P9PsxnUq7QA3UJzl_GNOd5qnmfWvBNwXtCc";
        parse(properties, token);
    }


    private static AuthInfo build(JwtProperties properties) {

//        String basic = "enVpaG91X2FkbWluX3VpenVpaG91X2FkbWluX3VpenVpaG91X2FkbWluX3VpenVpaG91X2FkbWluX3VpOnp1aWhvdV9hZG1pbl91aV9zZWNyZXR6dWlob3VfYWRtaW5fdWl6dWlob3VfYWRtaW5fdWl6dWlob3VfYWRtaW5fdWl6dWlob3VfYWRtaW5fdWl6dWlob3VfYWRtaW5fdWk=";
        //lamp_web:lamp_web_secret
//        String basic = "enVpaG91X3VpOnp1aWhvdV91aV9zZWNyZXQ=";
//        String basic ="enVpaG91X2FkbWluX3VpOnp1aWhvdV9hZG1pbl91aV9zZWNyZXQ=";

        JwtUserInfo user = new JwtUserInfo();
        user.setAccount("姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50");
        user.setUserId(Long.MAX_VALUE);
        user.setName("姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50姓名长度不能超过50");
        AuthInfo authInfo = new TokenUtil(properties).createAuthInfo(user, null);
        System.out.println(authInfo);
        System.out.println(authInfo.getToken());
        System.out.println(authInfo.getToken().length());
        return authInfo;

    }

    private static void parse(JwtProperties properties, String token) {
        AuthInfo authInfo = new TokenUtil(properties).parseRefreshToken(token);

        System.out.println(authInfo);
    }

}
