package ua.yaroslav.auth2.auth.token;

public class URLBuilder {
    private String url;


    public URLBuilder() {

    }

    public URLBuilder setRedirect(String redirect) {
        url = redirect;
        return this;
    }


    public URLBuilder setCode(String code) {
        url = url + "?code=" + code;
        return this;
    }

    public URLBuilder setState(String state) {
        url = url + "&state=" + state;
        return this;
    }

    public String build() {
        return url;
    }
}