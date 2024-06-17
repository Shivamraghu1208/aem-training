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
                     $('#'+id).append('<div>'+success+'</div>');
                     },
                   error: function(error){
                    error.log(error);
                   }
                  });

 });