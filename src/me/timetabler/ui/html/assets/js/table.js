$('#staffModal').on('shown.bs.modal', function () {
  $('#staffAddButton').focus()
});
$('#subjectModal').on('shown.bs.modal', function () {
  $('#subjectAddButton').focus()
});

function addStaff() {
    var id = document.getElementById("staffID").value;
    var name = document.getElementById("staffName").value;
    displayStaff(id, name);
    var tmp = JSON.stringify({id, name});
    java.addStaff(tmp);
}

function displayStaff(id, name) {
    var table = document.getElementById("staffTable");
    var row = table.insertRow(table.rows.length);

    row.insertCell(0).innerHTML = id;
    row.insertCell(1).innerHTML = name;
}

function addSubject() {
    var id = document.getElementById("subjectID");
    var name = document.getElementById("subjectName");
    displaySubject(id, name);
}

function displaySubject(id, name) {
    var table = document.getElementById("subjectTable").value;
    var row = table.insertRow(table.rows.length).value;

    row.insertCell(0).innerHTML = id;
    row.insertCell(1).innerHTML = name;
}
