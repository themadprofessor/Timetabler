$(document).ready(function(){
    $("#tabs a").click(function(e){
        e.preventDefault();
        $(this).tab('show');
    });
});

function addSubject(e) {
    var name = document.getElementById("nameSubject").value;
    var id = document.getElementById("idSubject").value;

    var table = document.getElementById("listSubject");
    var row = table.insertRow(table.rows.length);

    var idCell = row.insertCell(0);
    idCell.innerHTML = id;

    var nameCell = row.insertCell(1);
    nameCell.innerHTML = name;

    java.addSubject(name.value, id);
}

function addTeacher(e) {
    var name = document.getElementById("nameTeacher").value;
    var id = document.getElementById("idTeacher").value;

    var table = document.getElementById("listTeacher");
    var row = table.insertRow(table.rows.length);

    var idCell = row.insertCell(0);
    idCell.innerHTML = id;

    var nameCell = row.insertCell(1);
    nameCell.innerHTML = name;

    java.addTeacher(name, id);
}
