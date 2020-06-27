$(document).ready(function() {
    $("#nav-ador-registration").addClass("active");
    setupMenu();
    setupCoverage();
});

function notRegisterClick() {
    alert("Ön végül úgy döntött, hogy mégsem jelentkezik az Örökimádásra.");
    window.location.pathname = "/adoration/"
}

function doRegisterClick() {
    //fill data
    $("#nameError").attr('style', 'display:none');
    $("#emailError").attr('style', 'display:none');
    $("#mobileError").attr('style', 'display:none');
    $("#dhcError").attr('style', 'display:none');
    var b = {}; //empty object
    b.name = $("#name").val();
    b.email = $("#email").val();
    b.mobile = $("#mobile").val();
    b.dayId = parseInt($("#daySelect").find(":selected").val());
    b.hourId = parseInt($("#hourSelect").find(":selected").val());
    b.method = parseInt($("#method").find(":selected").val());
    b.comment = $("#comment").val();
    b.coordinate = $("#coordinate").find(":selected").val();
    b.dhc = $("#dhc").find(":selected").val();
    //verification
    var eStr = "";
    var bad = 0;
    if (b.name.length == 0) {
        bad = 1;
        str = "Név megadása szükséges!";
        $("#nameError").removeAttr('style');
    } else {
        var patt = /^[a-zA-ZöüóőúéáűíÖÜÓŐÚÉÁŰÍ\.\, ]*$/
        if (!patt.test(b.name)) {
            bad = 1;
            str = "A megadott Névben el nem fogadható karakterek is vannak, kérjük a név javítását!";
            $("#nameError").removeAttr('style');
        }
    }
    if ((b.email.length > 0) && (!validateEmail(b.email))) {
        bad = 1;
        str = "A megadott e-mail cím nem helyes!";
        $("#emailError").removeAttr('style');
    }
    var patt = /^[0-9\+\- ]*$/;
    if ((b.mobile.length > 0) && (!patt.test(b.mobile))) {
        bad = 1;
        str = "A megadott telefonszám nem helyes!";
        $("#mobileError").removeAttr('style');
    }
    if (b.email.length + b.mobile.length == 0) {
        bad = 1;
        str = "Telefonszám és e-mail cím közül legalább az egyik megadása kötelező!";
        $("#emailError").removeAttr('style');
        $("#mobileError").removeAttr('style');
    }
    if (b.dhc.indexOf("yes") <= 0) {
        bad = 1;
        str = "Adatkezelési hozzájárulás nélkül a jelentkezést nem tudjuk elfogadni!";
        $("#dhcError").removeAttr('style');
    }
    if (bad > 0) {
        alert("Hiba az adatokban!\n" + str);
        window.scrollTo(0, 0);
        return;
    }
    //everything is ok, send registration request

}