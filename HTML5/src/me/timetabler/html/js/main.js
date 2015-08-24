$(document).ready(function(){
    $("#tabs a").click(function(e){
        e.preventDefault();
        $(this).tab('show');
    });
});

function Subject(name, id) {
    this.name = name;
    this.id = id;
}

function Teacher(name, id) {
    this.name = name;
    this.id = id;
}

function addSubject(e) {
    var subject = new Subject(document.getElementById("nameSubject").value, document.getElementById("idSubject").value);
    showSubject(subject.name, subject.id);
    java.addSubject(subject.name, subject.id);
}

function addTeacher(e) {
    var teacher = new Teacher(document.getElementById("nameTeacher").value, document.getElementById("idTeacher").value);
    showTeacher(teacher.name, teacher.id);
    java.addTeacher(teacher.name, teacher.id);
}

function showTeacher(name, id) {
    var table = document.getElementById("listTeacher");
    var row = table.insertRow(table.rows.length);

    var idCell = row.insertCell(0);
    idCell.innerHTML = id;

    var nameCell = row.insertCell(1);
    nameCell.innerHTML = name;
}

function showSubject(name, id) {
    var table = document.getElementById("listSubject");
    var row = table.insertRow(table.rows.length);

    var idCell = row.insertCell(0);
    idCell.innerHTML = subject.id;

    var nameCell = row.insertCell(1);
    nameCell.innerHTML = subject.name;
}
