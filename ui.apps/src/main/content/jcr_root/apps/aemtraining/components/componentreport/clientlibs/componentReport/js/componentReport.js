 document.addEventListener("DOMContentLoaded",()=>{


  var componentReport = document.querySelectorAll(".componentreport")
  for(let i=0;i<componentReport.length;i++){
  componentReport[i].querySelector('.button').addEventListener('click', function() {
		let value = componentReport[i].querySelector('.dropdown-value').value;
        if(value!=null)
        {
        let id= componentReport[i].querySelector(".response-container").getAttribute("id");
		let componentResourcePath= componentReport[i].querySelector(".path-container").dataset.path;
		let apiUrl = componentResourcePath+".json?Path="+value;
        let currentIndex = componentReport[i];
      console.log("apiUrl : "+apiUrl);
		$.ajax({
          method: 'get',
          url: apiUrl,
          success: function(success){
		  currentIndex.querySelector(".response-container").innerHTML="";
          for(var i=0;i<success.length;i++)
          {

            $('#'+id).append('<div>'+success[i]+'</div>');

          }
            },
          error: function(error){
           console.log(error);
          }
         })
        }
  });
  }

  });


