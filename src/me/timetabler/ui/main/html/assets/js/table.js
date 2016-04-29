//Initialise tooltips
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

/**
 * Adds a member of staff to the system from the wizard. The data (name, subject and hours) is pulled from the
 * corresponding entry elements (staffName, staffSubject, staffHours). If staffHours is not a number, or any data is
 * empty, the wizard will not be closed and the user will be informed.
 */
function addStaff() {
    try {
        var name = document.getElementById("staffName").value;
        var subjectSelect = document.getElementById("staffSubject");
        var subject = subjectSelect.options[subjectSelect.selectedIndex].value;
        var hours = document.getElementById("staffHours").value;
        if (!(typeof hours === 'number') || !((hours % 1) === 0)) {
            java.warning("Hours per week must be a number");
            return;
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

/**
 * Adds a subject to the system from the wizard. The data (name) is pulled from the corresponding entry elements
 * (subjectName). If any data is empty, the wizard will not be closed and the user will be informed.
 */
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

/**
 * Adds a year group to the system from the wizard. The data (name) is pulled from the corresponding entry elements
 * (yearName). If any data is empty, the wizard will not be closed and the user will be informed.
 */
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

/**
 * Adds a set to the system from the wizard. The data (name) is pulled from the corresponding entry elements
 * (setName). If any data is empty, the wizard will not be closed and the user will be informed.
 */
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

/**
 * Adds a subject set to the system from the wizard. The data (subject, set and year) is pulled from the corresponding
 * entry elements (classSubject, classSet, classYear). If any data is empty, the wizard will not be closed and the user
 * will be informed.
 */
function addClass() {
    try {
        var subject = document.getElementById("classSubject").value;
        var set = document.getElementById("classSet").value;
        var year = document.getElementById("classYear").value;
    } catch (err) {
        java.warning(err);
        alert("Failed to add class!\nEach field must be present!");
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

function addLesson() {
    try {
        var period = document.getElementById("lessonPeriod").value;
        var subjectSet = document.getElementById("lessonClass").value;
        var classroom = document.getElementById("lessonClassroom").value;
        var staff = document.getElementById("lessonStaff").value;
    } catch (err) {
        java.warning(err);
        alert("Failed to add lesson!\nThe period and class field must be present!")
        return;
    }

    var data = "[" + period + "," + subjectSet;
    if (classroom != -1) {
        data = "," + data + "," + classroom;
    }
    if (staff != -1) {
        data = "," + data + "," + staff;
    }
    data = "" + data + "]";
    var id = java.add("Lesson", data);
    java.debug("Adding lesson");
    java.verbose("  " + data);
    if (success == true) {
        addToTable("lessonTable", [id, period, subjectSet, classroom, staff]);
        $("#lessonModal").modal("hide");
    }
}

/**
 * Adds the given items to the given table and adds a remove button as the last element in the row. The remove button
 * will call removeRow(button) to remove the row.
 * @param tableName The id of the table to be added to.
 * @param items An array of elements to be added to the table.
 */
function addToTable(tableName, items) {
    var table = document.getElementById(tableName);
    var row = table.insertRow(table.rows.length);
    for (var i = 0; i < items.length; i++) {
        java.verbose("Adding cell with the contents [" + items[i] + "]");
        row.insertCell(i).innerHTML = items[i];
    }

    //The icon for the button must be an empty span element.
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

/**
 * Removes the row this button is in. The button's parent's parent must be a row element. The data in the row is also
 * removed from the system by calling java.remove(type, data).
 * @param button The button who's parent's parent is a row.
 */
function removeRow(button) {
    var row = button.parentNode.parentNode;
    var cells = row.cells;
    var type = button.name;
    var data = [];

    for (var i = 0; i < cells.length-1; i++) {
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

/**
 * Adds the given text and value combination to the given select element. The text will be displayed to the user and the
 * value will be used for processing e.g. the ID.
 * @param selectName The ID of the select element to add to.
 * @param text The text to be displayed for the new entry.
 * @param value The value for the new entry.
 */
function addToSelect(selectName, text, value) {
    var options = document.getElementById(selectName);
    options[options.length] = new Option(text, value);
}

/**
 * Sets the global success variable's value to the given value. To be used by the Java bridge as it is more reliable
 * than direct modification.
 * @param value The value to se the success variable to be.
 *
 */
function setSuccess(value) {
    success = value;
}

/**
 * Calls the Java bridge's method loadMap(). To be used by load map buttons.
 */
function loadMap() {
    java.loadMap();
}

/**
 * Calls the the Java bridge's method loadFromFile(dataType, tableName). To be used by bulk insert buttons.
 * @param dataType The data type in the file.
 * @param tableName The table the data will be put into.
 */
function loadFile(dataType, tableName) {
    java.loadFromFile(dataType, tableName);
}

/**
 * Clears all entries from the given table by clicking the remove button in the last cell of each row.
 * @param tableName The ID of the table to be cleared.
 */
function clearTable(tableName) {
    java.debug("Clearing table [" + tableName + ']');
    var rows = document.getElementById(tableName).rows;

    while (rows.length > 1) {
        var cells = rows[1].cells;
        java.verbose("Removing row with ID [" + cells[0].innerHTML + ']');
        var butt = cells[cells.length - 1].firstElementChild;
        butt.click();
    }
}

/**
 * Adds a building or classroom to the given
 * @param tableName
 * @param data
 */
function addToTableHideRmBut(tableName, data) {
    addToTable(tableName, data);

    var rows = document.getElementById(tableName).rows;
    var cells = rows[rows.length - 1].cells;
    java.verbose("Hidding remove button for row with ID [" + cells[0].innerHTML + ']');
    var butt = cells[cells.length - 1].firstElementChild;
    butt.style.display = 'none';
}

/**
 * Calls java.timetable to begin timetabling.
 */
function timetable() {
    java.timetable();
}

/**
 * Updates the given table with the given data.
 * @param tableName The id of the table to update.
 * @param newData An array of the new data.
 */
function updateTable(tableName, newData) {
    var rows = document.getElementById(tableName).rows;

    for (var i = 1; i < newData.length; i++) {
        var thisData = newData[i].split(",");

        for (var j = 1; j < rows.length; j++) {
            var cells = rows[j].cells;
            var id = cells[0].innerHTML;
            
            if (id == thisData[0]) {
                for (var k = 1; k < thisData.length; k++) {
                    cells[k].innerHTML = thisData[k];
                }
                break;
            }
        }
    }
}
