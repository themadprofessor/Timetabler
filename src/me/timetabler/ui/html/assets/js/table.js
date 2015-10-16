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
    displayStaff(id, name);
    java.addStaff(JSON.stringify({"id":id, "name":name}));
}

function displayStaff(id, name) {
    var table = document.getElementById("staffTable");
    var row = table.insertRow(table.rows.length);

    row.insertCell(0).innerHTML = id;
    row.insertCell(1).innerHTML = name;
}

function addSubject() {
    var id = document.getElementById("subjectID").value;
    var name = document.getElementById("subjectName").value;
    displaySubject(id, name);
    java.addSubject(JSON.stringify({"id":id, "name":name}));
}

function displaySubject(id, name) {
    var table = document.getElementById("subjectTable");
    var row = table.insertRow(table.rows.length);

    row.insertCell(0).innerHTML = id;
    row.insertCell(1).innerHTML = name;
}
