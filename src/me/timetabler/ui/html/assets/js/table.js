$('#staffModal').on('shown.bs.modal', function () {
  $('#staffAddButton').focus()
});
$('#subjectModal').on('shown.bs.modal', function () {
  $('#subjectAddButton').focus()
});

/*window.onload = function () {
    staff.foreach(function(s) = {
        displayStaff(s.id, s.name);
    });
}*/

function addStaff(e) {
    var id = document.getElementById("staffID").value;
    var name = document.getElementById("staffName").value;
    displayStaff(id, name);
}

function displayStaff(id, name) {
    var table = document.getElementById("staffTable");
    var row = table.insertRow(table.rows.length);

    row.insertCell(0).innerHTML = id;
    row.insertCell(1).innerHTML = name;
}

function addSubject(e) {
    var id = document.getElementById("subjectID");
    var name = document.getElementById("subjectName");
    displaySubject(id, name);
}

function displaySubject(id, name) {
    var table = document.getElementById("subjectTable");
    var row = table.insertRow(table.rows.length);

    row.insertCell(0).innerHTML = id;
    row.insertCell(1).innerHTML = name;
}
