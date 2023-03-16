var oTable;
$(document).ready( function () {
    oTable = $('#balanceGrid').DataTable();
    setActionToForm();
    getBalanceData();

});

function executeTransaction(id, amount) {
        $.ajax({
        contentType: "application/json",
  type: "POST",
  url: "transaction",
  data: `{
    "id": "`+id+`",
    "amount": "`+amount+`"
  }`,
  success: function (result) {
     console.log(result);
  },
  dataType: "json"
});
};

function setActionToForm(){
     $("#balance").submit(function(e) {
        // here's where you stop the default submit action of the form
        e.preventDefault();
        getBalanceData();
    });
}

function getBalanceData(){
     // collect form data
             var data = $("#balance").serializeArray();
             // Now execute your AJAX
             $.post('balance-data', data, function (response) {
                 handleBalanceData(response);
             }).fail(function(xhr, status, message) {
                 alert("Не смогли получить данные, попробуйте позже")
             });
}

function handleBalanceData(balanceGridData) {
    oTable
     .clear()
     .draw();
    for(i = 0; i<balanceGridData.balanceGridItem.length; i++){
        var balanceGridItem = balanceGridData.balanceGridItem[i];
        var idB = "b-" + i;
        var but = "";
        if(balanceGridItem.carNum != null && balanceGridItem.carNum != ""){
            but = '<button id="'+idB+'">Списать</button>';
        }
        oTable.row.add([balanceGridItem.fullName,
                  balanceGridItem.carNum,
                  balanceGridItem.amount,
                  but
                  ]).draw();
        $('#'+idB).click(createFunction(balanceGridItem.carNum, balanceGridItem.amount));
    }
}

function createFunction(id, amount){
  return function(){executeTransaction(id, amount);};
}
