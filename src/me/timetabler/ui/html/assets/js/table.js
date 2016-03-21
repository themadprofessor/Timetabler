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
    var name = document.getElementById("staffName").value;
    var subjectSelect = document.getElementById("staffSubject");
    var subject = subjectSelect.options[subjectSelect.selectedIndex].value;
    var hours = document.getElementById("staffHours").value;
    java.debug("Adding Staff [" + name + ',' + subject + ',' + hours + ']');
    var id = java.add("Staff", "[" + name + ',' + subject + ',' + hours + "]");
    if (success == true) {
        addToTable("staffTable", [id, name, subject, hours]);
    } else {
        $("#staffModal").modal("hide");
    }
}

function addSubject() {
    var name = document.getElementById("subjectName").value;
    var id = java.add("Subject", name);
    addToTable("subjectTable", [id, name]);
}

function addToTable(tableName, items) {
    java.debug("Adding row to [" + tableName + "]");

    var table = document.getElementById(tableName);
    var row = table.insertRow(table.rows.length);
    for (i = 0; i < items.length; i++) {
        java.verbose("Adding cell with the contents [" + items[i] + "]");
        row.insertCell(i).innerHTML = items[i];
    }

    var span = document.createElement("span");
    span.className = "glyphicon glyphicon-remove";

    var rmBut = document.createElement("button");
    rmBut.type = "button";
    rmBut.name = tableName.replace("Table", "");
    rmBut.value = "Remove";
    rmBut.className = "btn btn-noborder";
    rmBut.appendChild(span);
    rmBut.onclick = function() {
        removeRow(this);
    };

    row.insertCell(row.cells.length).appendChild(rmBut);
    java.debug("Added Row To [" + tableName + ']')
    return row;
}

function removeRow(button) {
    java.debug("Removing Row.");
    var row = button.parentNode.parentNode;
    var cells = row.cells;
    var type = button.name;
    var data = [];

    for (i = 0; i < cells.length-1; i++) {
        data.push(cells[i].innerHTML);
    }

    java.remove(type, data);
    var table = row.parentNode;
    table.removeChild(row);
}

function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}

function addToTableJava(tableName, items) {
    var row = addToTable(tableName, items);
}

function setSuccess(value) {
    success = true;
}
