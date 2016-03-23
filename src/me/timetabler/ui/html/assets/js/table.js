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
    try {
        var name = document.getElementById("staffName").value;
        var subjectSelect = document.getElementById("staffSubject");
        var subject = subjectSelect.options[subjectSelect.selectedIndex].value;
        var hours = document.getElementById("staffHours").value;
        if (!(typeof hours === 'number') || !((hours % 1) === 0)) {
            java.warning("Hours per week must be a number");

        }
    } catch (err) {
        java.warning(err);
        alert("Failed to add member of staff!\nName, subject and hours fields must be present!\nHours field must be an integer!");
    }

    java.debug("Adding Staff");
    java.verbose("  [" + name + ',' + subject + ',' + hours + "]");
    var id = java.add("Staff", "[" + name + ',' + subject + ',' + hours + "]");
    if (success == true) {
        addToTable("staffTable", [id, name, subject, hours]);
        $("#staffModal").modal("hide");
    }
    //No alert needed as success is false if exception in Java, which produces alert.
}

function addSubject() {
    try {
        var name = document.getElementById("subjectName").value;
        if (name == null || name == "") {
            alert("Failed to add subject!\nThe name field must be present!");
            return;
        }
    } catch (err) {
        java.warning(err);
        alert("Failed to add subject!\nThe name field must be present!");
        return;
    }

    var id = java.add("Subject", name);
    java.debug("Adding subject");
    java.verbose("  [" + id + ',' + name + "]");
    if (success == true) {
        addToTable("subjectTable", [id, name]);
        $("#subjectModal").modal("hide");
        document.getElementById("subjectName").value = "";
    }
    //No alert needed as success is false if exception in Java, which produces alert.
}

function addYear() {
    try {
        var name = document.getElementById("yearName").value;
        if (name == null || name == "") {
            alert("Failed to add year!\nThe name field must be present!");
            return;
        }
    } catch (err) {
        java.warning(err);
        alert("Failed to add year!\nThe name field must be present!");
        return;
    }

    var id = java.add("Year", name);
    java.debug("Adding year");
    java.verbose("  [" + id + ',' + name + "]");
    if (success == true) {
        addToTable("yearTable", [id, name]);
        $("#yearModal").modal("hide");
        document.getElementById("yearName").value = "";
    }
    //No alert needed as success is false if exception in Java, which produces alert.
}

function addSet() {
    try {
        var name = document.getElementById("setName").value;
        if (name == null || name == "") {
            alert("Failed to add set!\nThe name field must be present!");
            return;
        }
    } catch (err) {
        java.warning(err);
        alert("Failed to add set!\nThe name field must be present!");
        return;
    }

    var id = java.add("Set", name);
    java.debug("Adding set");
    java.verbose("  [" + id + ',' + name + "]");
    if (success == true) {
        addToTable("setTable", [id, name]);
        $("#setModal").modal("hide");
        document.getElementById("setName").value = "";
    }
    //No alert needed as success is false if exception in Java, which produces alert.
}

function addClass() {
    try {
        var subject = document.getElementById("classSubject").value;
        var set = document.getElementById("classSet").value;
        var year = document.getElementById("classYear").value;
    } catch (err) {
        java.warning(err);
        alert("Failed to add class!\nThe name field must be present!");
        return;
    }

    var id = java.add("Class", "[" + subject + ',' + set + ',' + year + "]");
    java.debug("Adding class");
    java.verbose("  [" + id + ',' + subject + ',' + set + ',' + year + "]");
    if (success == true) {
        addToTable("classTable", [id, subject, set, year]);
        $("#classModal").modal("hide");
        document.getElementById("className").value = "";
    }
    //No alert needed as success is false if exception in Java, which produces alert.
}

function addToTable(tableName, items) {
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
}

function removeRow(button) {
    var row = button.parentNode.parentNode;
    var cells = row.cells;
    var type = button.name;
    var data = [];

    for (i = 0; i < cells.length-1; i++) {
        java.verbose("Storing [" + cells[i].innerHTML + "]");
        data.push(cells[i].innerHTML);
    }

    java.remove(type, data);
    if (success == true) {
        var table = row.parentNode;
        table.removeChild(row);
        java.debug("Removed row")
    } else {
        alert("Failed to remove entry!");
    }
}

function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}

function setSuccess(value) {
    success = value;
}

function loadMap() {
    java.loadMap();
}
