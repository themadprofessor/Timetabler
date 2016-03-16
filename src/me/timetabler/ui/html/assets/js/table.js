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
    var subject = document.getElementById("").value;
    var id = java.add("Staff", "[" + name + ',' + subject + "]");
    addToTable("staffTable", [id, name, subject]);
}

function addSubject() {
    var name = document.getElementById("subjectName").value;
    var id = java.add("Subject", "[" + name + "]"));
    addToTable("subjectTable", [id, name]);
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
    row.id = id;
    java.debug("Added [" + id + "] To [" + tableName + ']')
    return row;
}

function removeRow(button) {
    java.out("Removing Row.");
    var row = button.parentNode.parentNode;
    var cells = row.cells;
    var type = button.name;
    var data = [];

    for (i = 0; i < cells.length-1; i++) {
        data.push(cells[i].innerHTML);
    }

    java.remove(type, data);
    if (java.success) {
        var table = row.parentNode;
        table.removeChild(row);
    }
}

function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}

function addToTableJava(tableName, items, id) {
    var row = addToTable(tableName, items, tableName + id);
}
