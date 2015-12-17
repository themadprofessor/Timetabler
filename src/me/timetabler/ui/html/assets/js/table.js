$('#staffModal').on('shown.bs.modal', function () {
  $('#staffAddButton').focus()
});
$('#subjectModal').on('shown.bs.modal', function () {
  $('#subjectAddButton').focus()
});
/*$('#staffName').keyup(function(event){
  if (event.keyCode == 13) {
    $('#staffSave').click();
  }
});*/

function addStaff() {
    var id = document.getElementById("staffID").value;
    var name = document.getElementById("staffName").value;
    addToTable("staffTable", [id, name]);
    java.add("Staff", JSON.stringify({"id":id, "name":name}))
}

function addSubject() {
    var id = document.getElementById("subjectID").value;
    var name = document.getElementById("subjectName").value;
    addToTable("subjectTable", [id, name]);
    java.add("Subject", JSON.stringify({"id":id, "name":name}));
}

function addClass() {
    var id = document.getElementById("classID").value;
    var subject = document.getElementById("classSubject").value;
    addToTable("classTable", [id, subject]);
    java.add("Class", JSON.stringify({"id":id, "subjectId":subject}));
}

function addToTable(tableName, items) {
    var table = document.getElementById(tableName);
    var row = table.insertRow(table.rows.length);
    for (i = 0; i < items.length; i++) {
        row.insertCell(i).innerHTML = items[i];
    }
}

function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}
