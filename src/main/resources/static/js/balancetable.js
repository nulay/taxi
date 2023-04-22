var oTable;
$(document).ready( function () {
    oTable = $('#balanceGrid').DataTable();
    setActionToForm();
    getBalanceData();

});

function executeTransaction(driverProfileId, amount) {
        $.ajax({
        contentType: "application/json",
  type: "POST",
  url: "transaction",
  data: `{
    "driverProfileId": "`+driverProfileId+`",
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
        if(balanceGridItem.cardNum != null && balanceGridItem.cardNum != "" && balanceGridItem.driverProfile != null){
            but = '<button id="'+idB+'" onclick="executeTransaction('+balanceGridItem.driverProfile.id+', '+balanceGridItem.amount+')">Списать</button>';
        }
        var balanceDriver = "";
        var fullName = "";
        if(balanceGridItem.driverProfile !=null){
            balanceDriver = balanceGridItem.driverProfile.balance;
            fullName =  balanceGridItem.driverProfile.driverFullName;
        }
        oTable.row.add([fullName,
                  balanceGridItem.cardNum,
                  balanceGridItem.amount,
                  balanceDriver,
                  but
                  ]).draw();
    }
}

