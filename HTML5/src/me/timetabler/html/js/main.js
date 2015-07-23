$(document).ready(function(){
    $("#tabs a").click(function(e){
        e.preventDefault();
        $(this).tab('show');
    });
});

function addSubject(e) {
    java.addSubject(document.getElementById("nameSubject").value, document.getElementById("idSubject").value);
}

function addTeacher(e) {
    java.addTeacher(document.getElementById("nameTeacher").value, document.getElementById("idTeacher").value);
}

function showTeachers()
