  document.addEventListener("DOMContentLoaded",()=>{
  document.querySelector('.button').addEventListener('click', function() {
      let dropdown = document.querySelector('.dropdown-value');
      let value = dropdown.value;

      let componentResourcePath= $('.path-container').data("path")

      let apiUrl = componentResourcePath+".json?Path="+value;

            $.ajax({
           method: 'get',
           url: apiUrl,
           success: function(success){
            $(".response-container").html('');
           for(var i=0;i<success.length;i++)
           {
              $(".response-container").append('<div>'+success[i]+'</div>');
           }
             },
           error: function(error){
            console.log(error);
           }
          })


      });

   });