var cTable;
var dTable;
var cardGridDataGlobal;
var driverDataGlobal;
var dialogDr;
var cardCodeGlobal={};
$(document).ready( function () {
    cTable = $('#cardGrid').DataTable();
    dTable = $('#driverGrid').DataTable();
    getDriverProfileData();
    getCardData();
    dialogDr = $("#driverDialog").dialog({autoOpen: false, height: 600, width: 800});
});

function deleteLink(cardCode, yandexId, numEl) {
        $.ajax({
        contentType: "application/json",
  type: "POST",
  url: "delete_link-card-yandex-profile",
  data: `{
    "cardCode": "`+cardCode+`",
    "yandexDriverProfile": "`+yandexId+`"
  }`,
  success: function (result) {
     console.log(result);
     updateCardTableDelete(numEl);
  },
  dataType: "json"
});
};

function addLink(cardCode, yandexId, numEl) {
        $.ajax({
        contentType: "application/json",
  type: "POST",
  url: "link-card-yandex-profile",
  data: `{
    "cardCode": "`+cardCode+`",
    "yandexDriverProfile": "`+yandexId+`"
  }`,
  success: function (result) {
     console.log(result);
     updateCardTable(numEl);
  },
  dataType: "json"
});
};

function getCardData(){
     // collect form data
             var data = null;
             // Now execute your AJAX
             $.get('card-data', data, function (response) {
                 createCardTable(response);
             }).fail(function(xhr, status, message) {
                 alert("Не смогли получить данные, попробуйте позже")
             });
}

function getDriverProfileData(){
     // collect form data
             var data = null;
             // Now execute your AJAX
             $.get('driver-profile-data', data, function (response) {
                 createDriverTable(response);
             }).fail(function(xhr, status, message) {
                 alert("Не смогли получить данные, попробуйте позже")
             });
}

function createCardTable(cardGridData) {
    cardGridDataGlobal = cardGridData;
    cTable
     .clear()
     .draw();
    for(i = 0; i<cardGridData.length; i++){
        var cardGridDataItem = cardGridData[i];
        addRow(cardGridDataItem, i);
    }
}

function addRow(cardGridDataItem, i){
var idB = "b-" + i;
        var st;
        var pt;
        if(cardGridDataItem.yandexId == null){
             st = 'style="display:none"';
             pt='';
        }else{
             st = '';
             pt='style="display:none"';
        }
        var yandexId = '<div><div id="ya_'+idB+'" '+st+'><label id="driverId_'+idB+'">' + cardGridDataItem.yandexId +'</label>'+
        '<button id="del_'+idB+'" onclick="delFunction('+i+')">Удалить связь с водителем</button></div>'+
        '<button id="attach_'+idB+'" onclick="attachFunction('+i+')" '+pt+'>Соединить с водителем в Яндекс</button></div>';

        cTable.row.add([cardGridDataItem.cardCode,
                  cardGridDataItem.monthNorm,
                  cardGridDataItem.dayNorm,
                  cardGridDataItem.dayNormAmount,
                  cardGridDataItem.status,
                  cardGridDataItem.actionDate,
                  cardGridDataItem.driver,
                  cardGridDataItem.carNum,
                  yandexId
                  ]).draw();
}

function delFunction(numEl){
   deleteLink(cardGridDataGlobal[numEl].cardCode, cardGridDataGlobal[numEl], numEl);
}

function attachFunction(numEl){
  cardCodeGlobal.cardCode = cardGridDataGlobal[numEl].cardCode;
  cardCodeGlobal.numEl=numEl;
  showDriverPopup();
}

function showDriverPopup(){
   dialogDr.dialog("open");
}

function createDriverTable(driverData){
    dTable
     .clear()
     .draw();
    driverDataGlobal = driverData.driverProfiles;
    for(i = 0; i<driverData.driverProfiles.length; i++){
        var driverItem = driverData.driverProfiles[i];
        var idB = "b-" + i;
        var button ='<button id="choice_'+idB+'" onclick="setDriver('+i+')">Принять</button>';

        dTable.row.add([driverItem.id,
                  driverItem.first_name,
                  driverItem.last_name,
                  driverItem.middle_name,
                  driverItem.balance,
                  button
                  ]).draw();
    }
}

function setDriver(numEl){
         driverItem = driverDataGlobal[numEl];

         addLink(cardCodeGlobal.cardCode, driverItem.id, numEl);
}

function updateCardTable(numEl){
      driverItem = driverDataGlobal[numEl];
      dialogDr.dialog("close");

      $('#driverId_'+cardCodeGlobal.numEl).text(driverItem.id);

      var row = cTable.row( $('#ya_b-'+cardCodeGlobal.numEl).parents('tr') );
      var rowNode = row.node();
      row.remove();

      var cardGridDataItem = cardGridDataGlobal[cardCodeGlobal.numEl]
      cardGridDataItem.yandexId = driverItem.id;
      addRow(cardGridDataItem, cardCodeGlobal.numEl);
      cardCodeGlobal={};
}

function updateCardTableDelete(numEl){
      $('#driverId_'+cardCodeGlobal.numEl).text("");

      var row = cTable.row( $('#ya_b-'+numEl).parents('tr') );
      var rowNode = row.node();
      row.remove();

      var cardGridDataItem = cardGridDataGlobal[numEl]
      cardGridDataItem.yandexId = null;
      addRow(cardGridDataItem, numEl);
}