document.querySelector('.submit-button-val').addEventListener('click',function() {
const token = document.getElementById('token').value;
const id= document.querySelector(".response-value-container").getAttribute("id");
 const apiUrl="/bin/getToken.key.json";

   $.ajax({
                   method: 'get',
                   url: apiUrl ,
                   contentType: 'application/json',
                  data: {
                    token:token
                  },
                   success: function(success){
                     document.querySelector(".response-value-container").innerHTML="";
                      let content = '<div>';
                                 if (success.result.status === "Valid Token") {
                                     content += '<ul>' +
                                         '<li>Name: ' + success.result.name + '</li>' +
                                         '<li>Email: ' + success.result.email + '</li>' +
                                         '<li>Status: ' + success.result.status + '</li>' +
                                         '</ul>';
                                 } else {
                                     content += '<ul>' +
                                         '<li>:Message ' + success.result.message + '</li>' +
                                         '<li>Status: ' + success.result.status + '</li>' +
                                         '</ul>';
                                 }
                                 content += '</div>';
                     $('#'+id).append('<div>'+content+'</div>');
                     },
                   error: function(error){
                    error.log(error);
                   }
                  });

 });