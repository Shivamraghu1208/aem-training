document.querySelector('.submit-button').addEventListener('click', function() {
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const componentResourcePath = document.querySelector(".get-path").dataset.path;
    let apiUrl = componentResourcePath + ".key.json";
    let id = document.querySelector(".response-token-container").getAttribute("id");

    $.ajax({
        method: 'get',
        url: apiUrl,
        contentType: 'application/json',
        data: {
            name: name,
            email: email,
        },
        success: function(success) {
            document.querySelector(".response-token-container").innerHTML = "";
            let content = '<div>';
            if (success.result.status === "Already present") {
                content += '<ul>' +
                    '<li>Name: ' + success.result.name + '</li>' +
                    '<li>Email: ' + success.result.email + '</li>' +
                    '<li>Token: ' + success.result.token + '</li>' +
                    '<li>Status: ' + success.result.status + '</li>' +
                    '</ul>';
            } else {
                content += '<ul>' +
                    '<li> New Token: ' + success.result.new_Token + '</li>' +
                    '<li>Status: ' + success.result.status + '</li>' +
                    '</ul>';
            }
            content += '</div>';
            $('#' + id).append(content);
        },
        error: function(error) {
            console.log(error);
        }
    });
});
