document.addEventListener("DOMContentLoaded", function(){
    document.getElementById("login-button").addEventListener("click", login);
});

var login = function(){
    var username = document.getElementById("username").value.toString();
    var password = document.getElementById("password").value.toString();

    var data = new FormData();
    data.append("response_type","code");
    data.append("user_login", username);
    data.append("user_pass", password);
    data.append("client_id", "client");
    data.append("redirect_uri","https://developers.google.com/oauthplayground");

    var request = new XMLHttpRequest;
    request.onload = function() {

    };
    request.open("POST", "/auth");
    request.send(data);
};