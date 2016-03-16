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
    var row = addToTable("subjectTable", [id, name]);
    addRemoveButton(row, "removeSubject(this)", "subjectTable." + id);
    java.add("Subject", JSON.stringify({"id":id, "name":name}));
}

function addClass() {
    var select = document.getElementById("classSubject");
    var id = document.getElementById("classID").value;
    var subject = select.value;
    var name = select.options[options.selectedIndex].text;
    addToTable("classTable", [id, name], id);
    java.add("Class", JSON.stringify({"id":id, "subjectId":subject}));
}

function addToTable(tableName, items, id) {
    var table = document.getElementById(tableName);
    var row = table.insertRow(table.rows.length);
    for (i = 0; i < items.length; i++) {
        row.insertCell(i).innerHTML = items[i];
    }

    var rmBut = document.createElement("button");
    rmBut.type = "button";
    rmBut.value = "Remove";
    rmBut.class = "btn btn-noborder";
    rmBut.onclick = function() {
        alert("Whoop");
    }

    row.insertCell(row.cells.length).appendChild(rmBut);
    row.id = id;
    java.debug("Added [" + id + "] To [" + tableName + ']')
    return row;
}

function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}

function addToTableJava(tableName, items, id) {
    var row = addToTable(tableName, items, tableName + id);
}
