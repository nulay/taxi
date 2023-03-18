 $(document).ready( function () {
     switchRadioButton();
         $("input[name='writeOffGasTime.scheduler']").click(function(){
             switchRadioButton();
         });
 });


function switchRadioButton(){
if( $('#week').is(':checked') ){
    $('#weekDayView').show();
}
else{
     $('#weekDayView').hide();
}
if( $('#month').is(':checked') ){
    $('#monthDayView').show();
}
else{
     $('#monthDayView').hide();
}
}
