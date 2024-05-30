 document.addEventListener("DOMContentLoaded",()=>{
 document.querySelector('.button').addEventListener('click', function() {
     let dropdown = document.querySelector('.dropdown-value');
     let value = dropdown.value;
     console.log(value);
          let apiUrl = "http://localhost:4502/content/aemtraining/Student/ss/jcr:content/root/container/componentreport.json?Path="+value;

         $.ajax({
          method: 'get',
          url: apiUrl,
          success: function(success){
           $(".response-container").html('');
          for(var i=0;i<success.length;i++)
          {
          console.log(success[i]);
          $(".response-container").append('<div>'+success[i]+'</div>');
          }
            },
          error: function(error){
          console.log(error);
          }
         })

     });

  });


