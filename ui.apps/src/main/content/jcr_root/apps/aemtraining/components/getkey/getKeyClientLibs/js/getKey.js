       document.querySelector('.submit-button').addEventListener('click',function() {
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const componentResourcePath= document.querySelector(".get-path").dataset.path;
        let apiUrl=componentResourcePath+".key.json";
        let id=document.querySelector(".response-token-container").getAttribute("id");

        $.ajax({
                  method: 'get',
                  url: apiUrl ,
                  contentType: 'application/json',
                 data: {
                    name:name,
                    email:email,
                 },
                  success: function(success){
                    document.querySelector(".response-token-container").innerHTML="";
        		        $('#'+id).append('<div>'+success+'</div>');
                    },
                  error: function(error){
                   error.log(error);
                  }
                 })


    });