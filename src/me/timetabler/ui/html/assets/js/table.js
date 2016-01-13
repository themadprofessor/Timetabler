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
    var id = document.getElementById("classID").value;
    var subject = document.getElementById("classSubject").value;
    addToTable("classTable", [id, subject]);
    java.add("Class", JSON.stringify({"id":id, "subjectId":subject}));
}

function addToTable(tableName, items, id) {
    var table = document.getElementById(tableName);
    var row = table.insertRow(table.rows.length);
    for (i = 0; i < items.length; i++) {
        row.insertCell(i).innerHTML = items[i];
    }
    row.id = id;
    java.debug("Added [" + id + "] To [" + tableName + ']')
    return row;
}

function addRemoveButton(row, delFunc, id) {
    var align = document.createElement('div');
    align.align = "right";
    var icon = document.createElement('span');
    icon.className = "glyphicon glyphicon-remove"
    var delBtn = document.createElement('button');
    delBtn.type = "button";
    delBtn.className = "btn btn-noborder"
    delBtn.appendChild(icon);
    delBtn.setAttribute("onclick", delFunc);
    delBtn.id = id;
    align.appendChild(delBtn);
    row.insertCell(row.cells.length).appendChild(align);
}

function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}

function addToTableJava(tableName, items, id) {
    var row = addToTable(tableName, items, tableName + id);
    addRemoveButton(row, "removeSubject(this)", id);
}

function removeFromTable(tableName, ids) {
    var table = document.getElementById(tableName);
    java.debug("Found Table");
    for (i = 0; i < ids.length; i++) {
        table.removeChild(document.getElementById(ids[i]));
    }
    java.debug("Removed [" + ids.length + "] Values From [" + tableName + ']')
}

function removeSubject(obj) {
    java.debug("Removing [" + obj.id + "]");
    removeFromTable("subjectTable", [obj.id]);
    java.remove("Subject", obj);
}
