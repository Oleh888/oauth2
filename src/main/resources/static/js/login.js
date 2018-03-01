document.addEventListener("DOMContentLoaded", function(){
    document.getElementById("login-button").addEventListener("click", login);
});

var login = function(){
    var data = new FormData();
    data.append("user_login", document.getElementById("username").value.toString());
    data.append("user_pass", document.getElementById("password").value.toString());
    data.append("scope","read");
    data.append("redirect_uri","https://developers.google.com/oauthplayground");
    data.append("response_type","code");
    data.append("client_id", "client");
    data.append("access_type", "offline");

    var request = new XMLHttpRequest;
    request.onload = function() {
        console.log("done!");
        console.log(request.responseText);
    };
    request.open("POST", "/auth");
    request.send(data);
};